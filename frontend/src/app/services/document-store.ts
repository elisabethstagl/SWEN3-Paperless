import { inject, Injectable, signal } from '@angular/core';
import { PaperlessDocument } from '../models/paperless-document';
import { DocumentApiService } from './document-api-service';

@Injectable({
  providedIn: 'root',
})
export class DocumentStore {

  private documentApiService = inject(DocumentApiService)

  private readonly _documents = signal<PaperlessDocument[]>([]);
  readonly documents = this._documents.asReadonly();

  constructor() {
    this.documentApiService.getAll().subscribe((documents) => {
      this._documents.set(documents);
    });
  }

  delete(id: string) {
    this.documentApiService.delete(id).subscribe(() => {
      this._documents.update((docs) => docs.filter((d) => d.id !== id));
    });
  }

  upload(file: File) {
    this.documentApiService.upload(file).subscribe((doc) => {
      this._documents.update((docs) => [...docs, doc]);
    });
  }

}
