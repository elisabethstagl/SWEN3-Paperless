import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDivider } from '@angular/material/list';
import { DocumentContainer } from '../../components/document-container/document-container';
import { DocumentStore } from '../../services/document-store';
import { NoteApiService } from '../../services/note-api-service';

@Component({
  selector: 'app-home',
  imports: [MatButtonModule, MatIconModule, MatDivider, DocumentContainer],
  templateUrl: './home.html',
  styleUrl: './home.css',
  host: {
    '(document:dragover)': 'onDragOver($event)',
    '(document:drop)': 'onDrop($event)',
  },
})
export class Home {
  readonly documentStore = inject(DocumentStore)
  readonly noteApiService = inject(NoteApiService)
  
  constructor() {
    this.noteApiService.addNote("3bee76b4-5a7b-4ae5-a9a0-ac3188a2207d", "content").subscribe(response => {
      console.log(response)
    })
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
      this.documentStore.upload(file);
    }
  }
}
