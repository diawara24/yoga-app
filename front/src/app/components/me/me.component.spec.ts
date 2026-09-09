import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/core/service/session.service';
import { UserService } from 'src/app/core/service/user.service';
import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: { getById: jest.Mock; delete: jest.Mock };
  let router: { navigate: jest.Mock };

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
      token: 'jwt',
      type: 'Bearer',
      username: 'yoga@studio.com',
      firstName: 'Admin',
      lastName: 'Admin',
    },
    logOut: jest.fn(),
  };

  beforeEach(async () => {
    userService = {
      getById: jest.fn().mockReturnValue(of({
        id: 1,
        email: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
        password: '',
        createdAt: new Date(),
      })),
      delete: jest.fn(),
    };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [MeComponent, NoopAnimationsModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: userService },
        { provide: Router, useValue: router },
        { provide: MatSnackBar, useValue: { open: jest.fn() } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load current user on init', () => {
    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user?.email).toBe('yoga@studio.com');
  });

  it('should navigate to sessions on back', () => {
    component.back();
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});
