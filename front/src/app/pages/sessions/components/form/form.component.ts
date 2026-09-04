import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { getErrorMessage } from '../../../../core/utils/error-message.util';
import { MaterialModule } from "../../../../shared/material.module";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-form',
  imports: [CommonModule, MaterialModule],
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private matSnackBar = inject(MatSnackBar);
  private sessionApiService = inject(SessionApiService);
  private sessionService = inject(SessionService);
  private teacherService = inject(TeacherService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  public onUpdate: boolean = false;
  public sessionForm: FormGroup | undefined;
  public teachers$ = this.teacherService.all();
  private id: string | undefined;

  ngOnInit(): void {
    const sessionInformation = this.sessionService.sessionInformation;
    if (!sessionInformation?.admin) {
      this.router.navigate(['/sessions']);
      return;
    }

    const url = this.router.url;
    if (url.includes('update')) {
      const id = this.route.snapshot.paramMap.get('id');
      if (!id) {
        this.router.navigate(['/sessions']);
        return;
      }

      this.onUpdate = true;
      this.id = id;
      this.sessionApiService
        .detail(this.id)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (session: Session) => this.initForm(session),
          error: (error: unknown) => {
            this.showError(error, 'Impossible de charger la session à modifier.');
            this.router.navigate(['/sessions']);
          },
        });
    } else {
      this.initForm();
    }
  }

  public submit(): void {
    const session = this.sessionForm?.value as Session;

    if (!this.onUpdate) {
      this.sessionApiService
        .create(session)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (_: Session) => this.exitPage('Session créée avec succès.'),
          error: (error: unknown) => this.showError(error, 'Impossible de créer la session.'),
        });
      return;
    }

    if (!this.id) {
      this.router.navigate(['/sessions']);
      return;
    }

    this.sessionApiService
      .update(this.id, session)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (_: Session) => this.exitPage('Session mise à jour avec succès.'),
        error: (error: unknown) => this.showError(error, 'Impossible de mettre à jour la session.'),
      });
  }

  private initForm(session?: Session): void {
    this.sessionForm = this.fb.group({
      name: [
        session ? session.name : '',
        [Validators.required, Validators.maxLength(50)]
      ],
      date: [
        session ? new Date(session.date).toISOString().split('T')[0] : '',
        [Validators.required]
      ],
      teacher_id: [
        session ? session.teacher_id : '',
        [Validators.required]
      ],
      description: [
        session ? session.description : '',
        [
          Validators.required,
          Validators.maxLength(2500)
        ]
      ],
    });
  }

  private exitPage(message: string): void {
    this.matSnackBar.open(message, 'Fermer', { duration: 3000 });
    this.router.navigate(['sessions']);
  }

  private showError(error: unknown, fallback: string): void {
    this.matSnackBar.open(getErrorMessage(error, fallback), 'Fermer', { duration: 4000 });
  }
}
