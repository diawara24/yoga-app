import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/service/auth.service';
import { MessageResponse } from '../../core/models/messageResponse.interface';
import { RegisterRequest } from '../../core/models/registerRequest.interface';
import { getErrorMessage } from '../../core/utils/error-message.util';
import { MaterialModule } from "../../shared/material.module";

@Component({
  selector: 'app-register',
  imports: [MaterialModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  public errorMessage = '';

  public form = this.fb.group({
    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],
    firstName: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20)
      ]
    ],
    lastName: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20)
      ]
    ],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(40)
      ]
    ]
  });


  public submit(): void {
    this.errorMessage = '';
    const registerRequest = this.form.value as RegisterRequest;
    this.authService.register(registerRequest)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (_response: MessageResponse) => this.router.navigate(['/login']),
        error: (error: unknown) => {
          this.errorMessage = getErrorMessage(
            error,
            "Inscription impossible. Vérifiez les informations saisies."
          );
        },
      });
  }

}
