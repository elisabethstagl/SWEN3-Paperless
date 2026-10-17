import {Component, inject, signal} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {DocumentContainer} from '../../components/document-container/document-container';
import {DocumentStore} from '../../services/document-store';
import {Header} from '../../components/header/header';
import {NotificationService} from '../../services/notification-service';

@Component({
  selector: 'app-home',
  imports: [MatButtonModule, MatIconModule, DocumentContainer, Header],
  templateUrl: './home.html',
  styleUrl: './home.css',
  host: {
    '(document:dragover)': 'onDragOver($event)',
    '(document:drop)': 'onDrop($event)',
  },
})
export class Home {
  readonly documentStore = inject(DocumentStore)
  private readonly MAX_FILE_SIZE = 10 * 1024 * 1024;
  private readonly notificationService = inject(NotificationService);

  constructor() {
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
  }


  onDrop(event: DragEvent) {
    event.preventDefault();
    this.uploadAll(event.dataTransfer?.files);
  }

  onFilesSelected(input: HTMLInputElement) {
    this.uploadAll(input.files);
    input.value = '';
  }


  private uploadAll(files?: FileList | null) {
    for (const file of Array.from(files ?? [])) {
      const error = this.validateFile(file);

      if (error !== null) {
        this.notificationService.showError(
          `${file.name}: ${error}`
        );
        continue;
      }

      this.documentStore.upload(file);
    }
  }

  private validateFile(file: File): string | null {
    if (file.size > this.MAX_FILE_SIZE) {
      return 'File size must not exceed 10 MB';
    }

    if (file.type !== 'application/pdf') {
      return 'Only PDF files are allowed';
    }

    return null;
  }
}
