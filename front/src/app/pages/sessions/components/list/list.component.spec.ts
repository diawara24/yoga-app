import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { SessionService } from 'src/app/core/service/session.service';
import { ListComponent } from './list.component';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

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
    await TestBed.configureTestingModule({
      imports: [ListComponent, RouterTestingModule, NoopAnimationsModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        {
          provide: SessionApiService,
          useValue: {
            all: jest.fn().mockReturnValue(of([
              {
                id: 1,
                name: 'Yoga',
                description: 'Morning',
                date: new Date(),
                teacher_id: 1,
                users: [],
              },
            ])),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expose current user from session', () => {
    expect(component.user?.admin).toBe(true);
  });
});
