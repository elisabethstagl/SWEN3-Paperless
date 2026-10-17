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

  constructor() {
    this.documentApiService.getAll().subscribe((documents) => {
      this._documents.set(documents);
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
