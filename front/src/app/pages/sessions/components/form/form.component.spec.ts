import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: { detail: jest.Mock; create: jest.Mock; update: jest.Mock };
  let router: { navigate: jest.Mock; url: string };

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
  };

  beforeEach(async () => {
    sessionApiService = {
      detail: jest.fn(),
      create: jest.fn().mockReturnValue(of({ id: 1, name: 'Yoga', description: 'desc', date: new Date(), teacher_id: 1, users: [] })),
      update: jest.fn(),
    };
    router = {
      navigate: jest.fn(),
      url: '/sessions/create',
    };

    await TestBed.configureTestingModule({
      imports: [FormComponent, NoopAnimationsModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: sessionApiService },
        { provide: TeacherService, useValue: { all: jest.fn().mockReturnValue(of([])) } },
        { provide: Router, useValue: router },
        { provide: MatSnackBar, useValue: { open: jest.fn() } },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => null,
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize create form', () => {
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeTruthy();
    expect(component.sessionForm?.valid).toBe(false);
  });

  it('should create session on submit', () => {
    component.sessionForm?.setValue({
      name: 'Yoga',
      date: '2026-01-01',
      teacher_id: 1,
      description: 'Morning yoga',
    });

    component.submit();

    expect(sessionApiService.create).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
