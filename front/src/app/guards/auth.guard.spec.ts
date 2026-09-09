import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { expect } from '@jest/globals';
import { SessionService } from '../core/service/session.service';
import { authGuard } from './auth.guard';
import { unauthGuard } from './unauth.guard';

describe('Guards', () => {
  const createUrlTree = jest.fn((commands: string[]) => ({ commands } as unknown as UrlTree));

  beforeEach(() => {
    localStorage.clear();
    createUrlTree.mockClear();

    TestBed.configureTestingModule({
      providers: [
        SessionService,
        {
          provide: Router,
          useValue: { createUrlTree },
        },
      ],
    });
  });

  afterEach(() => {
    localStorage.clear();
  });

  describe('authGuard', () => {
    it('should allow access when logged in', () => {
      const sessionService = TestBed.inject(SessionService);
      sessionService.isLogged = true;

      const result = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

      expect(result).toBe(true);
    });

    it('should redirect to login when not logged in', () => {
      const sessionService = TestBed.inject(SessionService);
      sessionService.isLogged = false;

      const result = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

      expect(createUrlTree).toHaveBeenCalledWith(['/login']);
      expect(result).toEqual({ commands: ['/login'] });
    });
  });

  describe('unauthGuard', () => {
    it('should allow access when not logged in', () => {
      const sessionService = TestBed.inject(SessionService);
      sessionService.isLogged = false;

      const result = TestBed.runInInjectionContext(() => unauthGuard({} as never, {} as never));

      expect(result).toBe(true);
    });

    it('should redirect to sessions when logged in', () => {
      const sessionService = TestBed.inject(SessionService);
      sessionService.isLogged = true;

      const result = TestBed.runInInjectionContext(() => unauthGuard({} as never, {} as never));

      expect(createUrlTree).toHaveBeenCalledWith(['/sessions']);
      expect(result).toEqual({ commands: ['/sessions'] });
    });
  });
});
