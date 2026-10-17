import {Component, inject, input, output, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {PaperlessDocument} from '../../models/paperless-document';
import {DocumentApiService} from '../../services/document-api-service';
import {DocumentStore} from '../../services/document-store';
import {HttpErrorResponse} from '@angular/common/http';
import {NotificationService} from '../../services/notification-service';

@Component({
  selector: 'app-document-metadata',
  imports: [DatePipe, ReactiveFormsModule, MatIconModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatTooltipModule],
  templateUrl: './document-metadata.html',
  styleUrl: './document-metadata.css',
})
export class DocumentMetadata {
  private readonly documentApi = inject(DocumentApiService);
  private readonly documentStore = inject(DocumentStore);
  private readonly fb = inject(FormBuilder);

  readonly document = input.required<PaperlessDocument>();
  readonly documentUpdated = output<PaperlessDocument>();
  readonly isEditing = signal(false);
  readonly isSaving = signal(false);
  private readonly notificationService = inject(NotificationService);

  readonly form = this.fb.nonNullable.group({
    filename: ['', [
      Validators.required,
      Validators.maxLength(255),
      Validators.pattern(/.*\S.*/) // at least one char, no whitespaces or similar
    ]],
    author: ['', [Validators.maxLength(100)]],
    creator: ['', [Validators.maxLength(100)]],
  });

  private resetForm(): void {
    const document = this.document();
    this.form.reset({
      filename: document.filename,
      author: document.author ?? '',
      creator: document.creator ?? '',
    });
  }

  startEditing(): void {
    this.resetForm();
    this.isEditing.set(true);
  }

  cancelEditing(): void {
    this.resetForm();
    this.isEditing.set(false);
  }

  saveChanges(): void {
    if (this.isSaving()) return;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const document = this.document();
    const values = this.form.getRawValue();
    const updatedDocument: PaperlessDocument = {
      ...document,
      filename: values.filename,
      author: values.author || null,
      creator: values.creator || null,
    };

    this.isSaving.set(true);
    this.documentApi.update(document.id, updatedDocument).subscribe({
      next: savedDocument => {
        this.documentStore.updateDocument(savedDocument);
        this.documentUpdated.emit(savedDocument);
        this.isEditing.set(false);
        this.isSaving.set(false);

        this.notificationService.showSuccess(
          'Document saved successfully'
        );
      },
      error: (error: HttpErrorResponse) => {
        console.error('Failed to save document:', error);

        this.notificationService.showHttpError(
          error,
          'Could not save document'
        );

        this.isSaving.set(false);
      },
    });
  }
}
