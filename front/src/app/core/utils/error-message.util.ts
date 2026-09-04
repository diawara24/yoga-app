import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorBody } from '../models/apiErrorBody.interface';

const DEFAULT_ERROR_MESSAGE = 'Une erreur est survenue. Veuillez réessayer.';

export function getErrorMessage(error: unknown, fallback: string = DEFAULT_ERROR_MESSAGE): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }

  const body = error.error as ApiErrorBody | string | null;

  if (typeof body === 'string' && body.trim()) {
    return body;
  }

  if (body && typeof body === 'object') {
    if (body.detail?.trim()) {
      return body.detail;
    }
    if (body.message?.trim()) {
      return body.message;
    }
    if (body.fieldErrors?.trim()) {
      return body.fieldErrors;
    }
    if (body.title?.trim()) {
      return body.title;
    }
  }

  switch (error.status) {
    case 0:
      return 'Impossible de contacter le serveur. Vérifiez votre connexion.';
    case 401:
      return 'Identifiants incorrects ou session expirée.';
    case 403:
      return "Vous n'avez pas la permission d'effectuer cette action.";
    case 404:
      return 'Ressource introuvable.';
    case 409:
      return "Impossible d'effectuer cette opération (conflit de données).";
    case 422:
      return 'Les données envoyées ne sont pas valides.';
    case 500:
      return 'Erreur interne du serveur.';
    default:
      return fallback;
  }
}
