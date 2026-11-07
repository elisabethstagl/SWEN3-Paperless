import {Component, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';

import {Header} from '../../components/header/header';
import {PaperlessDocument} from '../../models/paperless-document';
import {DocumentApiService} from '../../services/document-api-service';
import {DocumentMetadata} from '../../components/document-metadata/document-metadata';
import {DocumentNotes} from '../../components/document-notes/document-notes';

@Component({
  selector: 'app-document-details',
  imports: [Header, MatIconModule, MatButtonModule, MatTooltipModule, DocumentMetadata, DocumentNotes],
  templateUrl: './document-details.html',
  styleUrl: './document-details.css',
})
export class DocumentDetails implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly documentApi = inject(DocumentApiService);
  private readonly sanitizer = inject(DomSanitizer);

  readonly document = signal<PaperlessDocument | null>(null);
  readonly isLoading = signal(true);
  readonly errorMessage = signal('');

  readonly fileUrl = signal<SafeResourceUrl | null>(null);
  readonly previewError = signal(false);
  private objectUrl: string | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage.set('No document ID provided.');
      this.isLoading.set(false);
      return;
    }
    this.loadDocument(id);
    this.loadFile(id);
  }

  ngOnDestroy(): void {
    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
    }
  }

  loadFile(id: string): void {
    this.documentApi.getFile(id).subscribe({
      next: file => {
        this.objectUrl = URL.createObjectURL(file);
        this.fileUrl.set(this.sanitizer.bypassSecurityTrustResourceUrl(this.objectUrl));
      },
      error: error => {
        console.error('Failed to load file:', error);
        this.previewError.set(true);
      },
    });
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
