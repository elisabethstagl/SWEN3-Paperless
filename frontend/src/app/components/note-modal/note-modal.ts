import {Component, inject, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from '@angular/material/dialog';
import {Note} from '../../models/paperless-note';
import {PaperlessDocument} from '../../models/paperless-document';
import {NoteApiService} from '../../services/note-api-service';
import {MatFormField, MatHint, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatDivider} from '@angular/material/divider';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatIconButton, MatMiniFabButton} from '@angular/material/button';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-note-modal',
  imports: [FormsModule, MatFormField, MatDialogTitle, MatDialogContent, MatLabel, MatInput, MatDivider, MatIcon, MatDialogClose, MatIconButton, MatTooltip, MatHint, MatMiniFabButton, MatButton],
  templateUrl: './note-modal.html',
  styleUrl: './note-modal.css',
})
export class NoteModal {
  private readonly noteApiService = inject(NoteApiService);
  readonly document = inject<PaperlessDocument>(MAT_DIALOG_DATA);

  readonly notes = signal<Note[]>([]);
  readonly newContent = signal('');
  readonly editingId = signal<string | null>(null);
  readonly editContent = signal('');

  constructor() {
    this.noteApiService.getAll(this.document.id).subscribe((notes) => {
      this.notes.set(notes);
    });
  }

  add() {
    const content = this.newContent().trim();
    if (!content || content.length > 500) return;

    this.noteApiService.add(this.document.id, content).subscribe((note) => {
      this.notes.update((notes) => [...notes, note]);
      this.newContent.set('');
    });
  }

  startEdit(note: Note) {
    this.editingId.set(note.id);
    this.editContent.set(note.content);
  }

  cancelEdit() {
    this.editingId.set(null);
  }

  saveEdit(note: Note) {
    const content = this.editContent().trim();
    if (!content || content.length > 500) return;

    this.noteApiService.update(this.document.id, note.id, content).subscribe((updated) => {
      this.notes.update((notes) => notes.map((n) => (n.id === updated.id ? updated : n)));
      this.editingId.set(null);
    });
  }

  delete(note: Note) {
    this.noteApiService.delete(this.document.id, note.id).subscribe(() => {
      this.notes.update((notes) => notes.filter((n) => n.id !== note.id));
    });
  }

}
