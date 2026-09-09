import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { LoginRequest } from '../models/loginRequest.interface';
import { MessageResponse } from '../models/messageResponse.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login', () => {
    const request: LoginRequest = { email: 'yoga@studio.com', password: 'test!1234' };
    const response: SessionInformation = {
      token: 'jwt',
      type: 'Bearer',
      id: 1,
      username: 'yoga@studio.com',
      firstName: 'Admin',
      lastName: 'Admin',
      admin: true,
    };

    service.login(request).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(response);
  });

  it('should register', () => {
    const request: RegisterRequest = {
      email: 'user@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password',
    };
    const response: MessageResponse = { message: 'User registered successfully!' };

    service.register(request).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(response);
  });
});
