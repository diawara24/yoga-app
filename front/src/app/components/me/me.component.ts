import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { MessageResponse } from '../../core/models/messageResponse.interface';
import { User } from '../../core/models/user.interface';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';
import { getErrorMessage } from '../../core/utils/error-message.util';
import { MaterialModule } from "../../shared/material.module";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-me',
  imports: [CommonModule, MaterialModule],
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss']
})
export class MeComponent implements OnInit {
  private router = inject(Router);
  private sessionService = inject(SessionService);
  private matSnackBar = inject(MatSnackBar);
  private userService = inject(UserService);
  private destroyRef = inject(DestroyRef);
  public user: User | undefined;


  ngOnInit(): void {
    const sessionInformation = this.sessionService.sessionInformation;
    if (!sessionInformation) {
      this.router.navigate(['/login']);
      return;
    }

    this.userService
      .getById(sessionInformation.id.toString())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user: User) => this.user = user,
        error: (error: unknown) => {
          this.matSnackBar.open(
            getErrorMessage(error, 'Impossible de charger votre profil.'),
            'Fermer',
            { duration: 4000 }
          );
        },
      });
  }

  public back(): void {
    this.router.navigate(['/sessions']);
  }

  public delete(): void {
    const confirmed = window.confirm('Voulez-vous vraiment supprimer votre compte ?');
    if (!confirmed) {
      return;
    }

    const sessionInformation = this.sessionService.sessionInformation;
    if (!sessionInformation) {
      this.router.navigate(['/login']);
      return;
    }

    this.userService
      .delete(sessionInformation.id.toString())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: MessageResponse) => {
          this.matSnackBar.open(response.message || 'Compte supprimé.', 'Fermer', { duration: 3000 });
          this.sessionService.logOut();
          this.router.navigate(['/']);
        },
        error: (error: unknown) => {
          this.matSnackBar.open(
            getErrorMessage(error, 'Impossible de supprimer le compte.'),
            'Fermer',
            { duration: 4000 }
          );
        },
      });
  }

}
