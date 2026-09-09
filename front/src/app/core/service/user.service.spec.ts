import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { MessageResponse } from '../models/messageResponse.interface';
import { User } from '../models/user.interface';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const user: User = {
    id: 1,
    email: 'yoga@studio.com',
    lastName: 'Admin',
    firstName: 'Admin',
    admin: true,
    password: '',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user by id', () => {
    service.getById('1').subscribe((result) => {
      expect(result).toEqual(user);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(user);
  });

  it('should delete user', () => {
    const response: MessageResponse = { message: 'Utilisateur supprimé' };

    service.delete('1').subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(response);
  });
});
