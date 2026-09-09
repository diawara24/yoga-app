import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: {
    detail: jest.Mock;
    delete: jest.Mock;
    participate: jest.Mock;
    unParticipate: jest.Mock;
  };
  let teacherService: { detail: jest.Mock };
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
  };

  beforeEach(async () => {
    sessionApiService = {
      detail: jest.fn().mockReturnValue(of({
        id: 1,
        name: 'Yoga',
        description: 'Morning',
        date: new Date(),
        teacher_id: 2,
        users: [1],
      })),
      delete: jest.fn(),
      participate: jest.fn(),
      unParticipate: jest.fn(),
    };
    teacherService = {
      detail: jest.fn().mockReturnValue(of({
        id: 2,
        firstName: 'John',
        lastName: 'Doe',
        createdAt: new Date(),
        updatedAt: new Date(),
      })),
    };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [DetailComponent, NoopAnimationsModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: sessionApiService },
        { provide: TeacherService, useValue: teacherService },
        { provide: Router, useValue: router },
        { provide: MatSnackBar, useValue: { open: jest.fn() } },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => (key === 'id' ? '1' : null),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load session and teacher on init', () => {
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(teacherService.detail).toHaveBeenCalledWith('2');
    expect(component.session?.name).toBe('Yoga');
    expect(component.teacher?.firstName).toBe('John');
    expect(component.isParticipate).toBe(true);
  });

  it('should navigate to sessions on back', () => {
    component.back();
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});
