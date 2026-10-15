import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';

import {Header} from '../../components/header/header';
import {PaperlessDocument} from '../../models/paperless-document';
import {DocumentApiService} from '../../services/document-api-service';
import {DocumentMetadata} from '../../components/document-metadata/document-metadata';
import {DocumentNotes} from '../../components/document-notes/document-notes';

@Component({
  selector: 'app-document-details',
  imports: [Header, MatIconModule, DocumentMetadata, DocumentNotes],
  templateUrl: './document-details.html',
  styleUrl: './document-details.css',
})
export class DocumentDetails implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly documentApi = inject(DocumentApiService);

  readonly document = signal<PaperlessDocument | null>(null);
  readonly isLoading = signal(true);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage.set('No document ID provided.');
      this.isLoading.set(false);
      return;
    }
    this.loadDocument(id);
  }

  loadDocument(id: string): void {
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.documentApi.getById(id).subscribe({
      next: document => {
        this.document.set(document);
        this.isLoading.set(false);
      },
      error: error => {
        console.error('Failed to load document:', error);
        this.errorMessage.set('Could not load document.');
        this.isLoading.set(false);
      },
    });
  }

  onDocumentUpdated(document: PaperlessDocument): void {
    this.document.set(document);
  }
}
