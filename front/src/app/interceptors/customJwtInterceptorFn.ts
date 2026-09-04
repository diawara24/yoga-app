import { HttpEvent, HttpHandlerFn, HttpRequest } from "@angular/common/http";
import { Observable } from "rxjs";
import { SessionService } from "../core/service/session.service";
import { inject } from "@angular/core";

export function customJwtInterceptorFn(request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const sessionService = inject(SessionService);
  const sessionInformation = sessionService.sessionInformation;

  if (sessionService.isLogged && sessionInformation) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${sessionInformation.token}`,
      },
    });
  }
  return next(request);
}
