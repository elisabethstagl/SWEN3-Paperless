import {inject, Injectable, signal} from '@angular/core';
import {PaperlessDocument} from '../models/paperless-document';
import {DocumentApiService} from './document-api-service';
import {HttpErrorResponse} from '@angular/common/http';
import {NotificationService} from './notification-service';

@Injectable({
  providedIn: 'root',
})
export class DocumentStore {

  private documentApiService = inject(DocumentApiService)

  private readonly _documents = signal<PaperlessDocument[]>([]);
  readonly documents = this._documents.asReadonly();
  private readonly notificationService = inject(NotificationService);

  private readonly _isLoading = signal(false);
  readonly isLoading = this._isLoading.asReadonly();

  private readonly _loadError = signal(false);
  readonly loadError = this._loadError.asReadonly();

  constructor() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    if (this._isLoading()) return;

    this._isLoading.set(true);
    this._loadError.set(false);

    this.documentApiService.getAll().subscribe({
      next: documents => {
        this._documents.set(documents);
        this._isLoading.set(false);
      },

      error: (error: HttpErrorResponse) => {
        console.error('Failed to load documents:', error);

        this._loadError.set(true);
        this._isLoading.set(false);

        this.notificationService.showHttpError(
          error,
          'Could not load documents'
        );
      }
    });
  }

  updateDocument(document: PaperlessDocument): void {
    this._documents.update(documents =>
      documents.map(existing =>
        existing.id === document.id ? document : existing
      )
    );
  }

  delete(id: string): void {
    this.documentApiService.delete(id).subscribe({
      next: () => {
        this._documents.update(
          docs => docs.filter(d => d.id !== id)
        );

        this.notificationService.showSuccess(
          'Document deleted successfully'
        );
      },

      error: (error: HttpErrorResponse) => {
        this.notificationService.showHttpError(
          error,
          'Could not delete document'
        );
      }
    });
  }

  upload(file: File) {
    this.documentApiService.upload(file).subscribe({
      next: (doc) => {
        this._documents.update((docs) => [...docs, doc]);
      },

      error: (error: HttpErrorResponse) => {
        this.notificationService.showHttpError(
          error,
          'Upload failed'
        );
      }
    });
  }

}
