import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap } from 'rxjs';
import { Teacher } from '../../../../core/models/teacher.interface';
import { MessageResponse } from '../../../../core/models/messageResponse.interface';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { getErrorMessage } from '../../../../core/utils/error-message.util';
import { MaterialModule } from "../../../../shared/material.module";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-detail',
  imports: [CommonModule, MaterialModule],
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  public session: Session | undefined;
  public teacher: Teacher | undefined;
  public isParticipate = false;
  public isAdmin = false;
  public sessionId = '';
  public userId = '';

  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private sessionApiService = inject(SessionApiService);
  private teacherService = inject(TeacherService);
  private matSnackBar = inject(MatSnackBar);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  constructor() {
    const sessionId = this.route.snapshot.paramMap.get('id');
    const sessionInformation = this.sessionService.sessionInformation;

    if (!sessionId || !sessionInformation) {
      this.router.navigate(['/sessions']);
      return;
    }

    this.sessionId = sessionId;
    this.isAdmin = sessionInformation.admin;
    this.userId = sessionInformation.id.toString();
  }

  ngOnInit(): void {
    if (!this.sessionId) {
      return;
    }
    this.fetchSession();
  }

  public back(): void {
    this.router.navigate(['/sessions']);
  }

  public delete(): void {
    const confirmed = window.confirm('Voulez-vous vraiment supprimer cette session ?');
    if (!confirmed) {
      return;
    }

    this.sessionApiService
      .delete(this.sessionId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: MessageResponse) => {
          this.matSnackBar.open(response.message || 'Session supprimée.', 'Fermer', { duration: 3000 });
          this.router.navigate(['sessions']);
        },
        error: (error: unknown) => {
          this.matSnackBar.open(
            getErrorMessage(error, 'Impossible de supprimer la session.'),
            'Fermer',
            { duration: 4000 }
          );
        },
      });
  }

  public participate(): void {
    this.sessionApiService.participate(this.sessionId, this.userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: MessageResponse) => {
          this.matSnackBar.open(response.message || 'Participation confirmée.', 'Fermer', { duration: 3000 });
          this.fetchSession();
        },
        error: (error: unknown) => {
          this.matSnackBar.open(
            getErrorMessage(error, 'Impossible de participer à cette session.'),
            'Fermer',
            { duration: 4000 }
          );
        },
      });
  }

  public unParticipate(): void {
    this.sessionApiService.unParticipate(this.sessionId, this.userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: MessageResponse) => {
          this.matSnackBar.open(response.message || 'Participation annulée.', 'Fermer', { duration: 3000 });
          this.fetchSession();
        },
        error: (error: unknown) => {
          this.matSnackBar.open(
            getErrorMessage(error, 'Impossible d\'annuler la participation.'),
            'Fermer',
            { duration: 4000 }
          );
        },
      });
  }

  private fetchSession(): void {
    const currentUserId = this.sessionService.sessionInformation?.id;

    this.sessionApiService
      .detail(this.sessionId)
      .pipe(
        tap((session: Session) => {
          this.session = session;
          this.isParticipate = currentUserId !== undefined
            && session.users.some(u => u === currentUserId);
        }),
        switchMap((session: Session) => this.teacherService.detail(session.teacher_id.toString())),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (teacher: Teacher) => this.teacher = teacher,
        error: (error: unknown) => {
          this.matSnackBar.open(
            getErrorMessage(error, 'Impossible de charger la session.'),
            'Fermer',
            { duration: 4000 }
          );
          this.router.navigate(['/sessions']);
        },
      });
  }

}
