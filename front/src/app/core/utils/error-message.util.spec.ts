import { HttpErrorResponse } from '@angular/common/http';
import { expect } from '@jest/globals';
import { getErrorMessage } from './error-message.util';

describe('getErrorMessage', () => {
  it('should return backend detail when present', () => {
    const error = new HttpErrorResponse({
      status: 400,
      error: { detail: 'Cet email est déjà utilisé.' },
    });

    expect(getErrorMessage(error)).toBe('Cet email est déjà utilisé.');
  });

  it('should return backend message when detail is missing', () => {
    const error = new HttpErrorResponse({
      status: 401,
      error: { message: 'Vous devez vous connecter pour accéder à cette page' },
    });

    expect(getErrorMessage(error)).toBe('Vous devez vous connecter pour accéder à cette page');
  });

  it('should return status fallback for 401 without body message', () => {
    const error = new HttpErrorResponse({
      status: 401,
      error: {},
    });

    expect(getErrorMessage(error)).toBe('Identifiants incorrects ou session expirée.');
  });

  it('should return custom fallback for unknown errors', () => {
    expect(getErrorMessage(new Error('boom'), 'Erreur personnalisée')).toBe('Erreur personnalisée');
  });
});
