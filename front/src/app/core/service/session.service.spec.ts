import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../models/sessionInformation.interface';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  const session: SessionInformation = {
    token: 'jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'yoga@studio.com',
    firstName: 'Admin',
    lastName: 'Admin',
    admin: true,
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in and persist session', () => {
    service.logIn(session);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(session);
    expect(localStorage.getItem('yoga_session')).toContain('jwt-token');
  });

  it('should log out and clear session', () => {
    service.logIn(session);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    expect(localStorage.getItem('yoga_session')).toBeNull();
  });

  it('should restore session from localStorage on init', () => {
    localStorage.setItem('yoga_session', JSON.stringify(session));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({});

    const restoredService = TestBed.inject(SessionService);

    expect(restoredService.isLogged).toBe(true);
    expect(restoredService.sessionInformation?.token).toBe('jwt-token');
  });
});
