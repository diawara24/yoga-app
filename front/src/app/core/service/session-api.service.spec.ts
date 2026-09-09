import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { MessageResponse } from '../models/messageResponse.interface';
import { Session } from '../models/session.interface';
import { SessionApiService } from './session-api.service';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const session: Session = {
    id: 1,
    name: 'Yoga',
    description: 'Morning yoga',
    date: new Date('2026-01-01'),
    teacher_id: 2,
    users: [1],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all sessions', () => {
    service.all().subscribe((sessions) => {
      expect(sessions).toEqual([session]);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush([session]);
  });

  it('should get session detail', () => {
    service.detail('1').subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(session);
  });

  it('should delete a session', () => {
    const response: MessageResponse = { message: 'Session supprimé' };

    service.delete('1').subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(response);
  });

  it('should participate to a session', () => {
    const response: MessageResponse = { message: 'Participation confirmée' };

    service.participate('1', '2').subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });
});
