import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly snackBar = inject(MatSnackBar);

  showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['snackbar-error']
    });
  }

  showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['snackbar-success']
    });
  }

  showHttpError(
    error: HttpErrorResponse,
    fallbackMessage = 'An unexpected error occurred'
  ): void {
    let message = fallbackMessage;

    if (error.status === 0) {
      message = 'Backend is not reachable';
    } else if (
      typeof error.error === 'string' &&
      error.error.trim()
    ) {
      message = error.error;
    } else if (error.status === 413) {
      message = 'File size must not exceed 10 MB';
    } else if (error.status === 404) {
      message = 'Document not found';
    }

    this.showError(message);
  }
}
