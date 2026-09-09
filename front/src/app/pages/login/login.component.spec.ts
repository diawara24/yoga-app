import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { SessionInformation } from 'src/app/core/models/sessionInformation.interface';
import { AuthService } from 'src/app/core/service/auth.service';
import { SessionService } from 'src/app/core/service/session.service';
import { MaterialModule } from 'src/app/shared/material.module';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: { login: jest.Mock };
  let sessionService: { logIn: jest.Mock };
  let router: { navigate: jest.Mock };

  const session: SessionInformation = {
    token: 'jwt',
    type: 'Bearer',
    id: 1,
    username: 'yoga@studio.com',
    firstName: 'Admin',
    lastName: 'Admin',
    admin: true,
  };

  beforeEach(async () => {
    authService = { login: jest.fn() };
    sessionService = { logIn: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        MaterialModule,
        ReactiveFormsModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should invalidate empty form', () => {
    expect(component.form.valid).toBe(false);
  });

  it('should login and navigate on success', () => {
    authService.login.mockReturnValue(of(session));
    component.form.setValue({ email: 'yoga@studio.com', password: 'test!1234' });

    component.submit();

    expect(authService.login).toHaveBeenCalled();
    expect(sessionService.logIn).toHaveBeenCalledWith(session);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should display backend error message on failure', () => {
    authService.login.mockReturnValue(
      throwError(() => new HttpErrorResponse({
        status: 401,
        error: { detail: 'Identifiants invalides' },
      }))
    );
    component.form.setValue({ email: 'yoga@studio.com', password: 'wrong' });

    component.submit();

    expect(component.errorMessage).toBe('Identifiants invalides');
  });
});
