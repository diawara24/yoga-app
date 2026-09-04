import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../core/service/session.service';

export const unauthGuard: CanActivateFn = () => {
  const sessionService = inject(SessionService);
  const router = inject(Router);

  if (sessionService.isLogged) {
    return router.createUrlTree(['/sessions']);
  }

  return true;
};
