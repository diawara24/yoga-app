import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SessionInformation } from '../models/sessionInformation.interface';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  private static readonly STORAGE_KEY = 'yoga_session';

  public isLogged = false;
  public sessionInformation: SessionInformation | undefined;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor() {
    this.restoreSession();
  }

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public logIn(user: SessionInformation): void {
    this.sessionInformation = user;
    this.isLogged = true;
    localStorage.setItem(SessionService.STORAGE_KEY, JSON.stringify(user));
    this.next();
  }

  public logOut(): void {
    this.sessionInformation = undefined;
    this.isLogged = false;
    localStorage.removeItem(SessionService.STORAGE_KEY);
    this.next();
  }

  private restoreSession(): void {
    const storedSession = localStorage.getItem(SessionService.STORAGE_KEY);
    if (!storedSession) {
      return;
    }

    try {
      const session = JSON.parse(storedSession) as SessionInformation;
      if (!session?.token) {
        localStorage.removeItem(SessionService.STORAGE_KEY);
        return;
      }

      this.sessionInformation = session;
      this.isLogged = true;
      this.next();
    } catch {
      localStorage.removeItem(SessionService.STORAGE_KEY);
    }
  }

  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
