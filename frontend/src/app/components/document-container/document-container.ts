import { Component, inject, signal } from '@angular/core';
import { DocumentCard } from '../document-card/document-card';
import { DocumentStore } from '../../services/document-store';

@Component({
  selector: 'app-document-container',
  imports: [DocumentCard],
  templateUrl: './document-container.html',
  styleUrl: './document-container.css',
})
export class DocumentContainer {
  readonly documentStore = inject(DocumentStore)
}
