import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from 'src/app/core/service/auth.service';
import { MaterialModule } from 'src/app/shared/material.module';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: { register: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    authService = { register: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        MaterialModule,
        ReactiveFormsModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should invalidate empty form', () => {
    expect(component.form.valid).toBe(false);
  });

  it('should register and navigate to login on success', () => {
    authService.register.mockReturnValue(of({ message: 'User registered successfully!' }));
    component.form.setValue({
      email: 'user@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password',
    });

    component.submit();

    expect(authService.register).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should display backend error message on failure', () => {
    authService.register.mockReturnValue(
      throwError(() => new HttpErrorResponse({
        status: 400,
        error: { detail: 'Cet email est déjà utilisé.' },
      }))
    );
    component.form.setValue({
      email: 'user@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password',
    });

    component.submit();

    expect(component.errorMessage).toBe('Cet email est déjà utilisé.');
  });
});
