import {Component, inject, input, OnInit, signal} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {NoteApiService} from '../../services/note-api-service';
import {Note} from '../../models/paperless-note';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDeleteDialog} from '../confirm-delete-dialog/confirm-delete-dialog';


@Component({
  selector: 'app-document-notes',
  imports: [MatIconModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatTooltipModule],
  templateUrl: './document-notes.html',
  styleUrl: './document-notes.css',
})
export class DocumentNotes implements OnInit {
  private readonly noteApi = inject(NoteApiService);

  readonly documentId = input.required<string>();
  readonly notes = signal<Note[]>([]);
  readonly editingNoteId = signal<string | null>(null);
  readonly savingNoteId = signal<string | null>(null);
  readonly isAddingNote = signal(false);
  readonly isSavingNewNote = signal(false);
  readonly errorMessage = signal('');

  private readonly dialog = inject(MatDialog);

  noteContent = '';
  newNoteContent = '';

  ngOnInit(): void {
    this.loadNotes();
  }

  loadNotes(): void {
    this.noteApi.getAll(this.documentId()).subscribe({
      next: notes => this.notes.set(notes),
      error: error => {
        console.error('Failed to load notes:', error);
        this.errorMessage.set('Could not load notes.');
      },
    });
  }

  startEditingNote(note: Note): void {
    this.editingNoteId.set(note.id);
    this.noteContent = note.content;
  }

  cancelEditingNote(): void {
    this.editingNoteId.set(null);
    this.noteContent = '';
  }

  saveNote(note: Note): void {
    if (
      this.savingNoteId() !== null ||
      !this.noteContent.trim() ||
      this.noteContent.length > 500
    ) {
      return;
    }

    const content = this.noteContent.trim();
    this.savingNoteId.set(note.id);
    this.errorMessage.set('');
    this.noteApi.update(this.documentId(), note.id, content).subscribe({
      next: updatedNote => {
        this.notes.update(notes => notes.map(existing => existing.id === note.id ? updatedNote : existing));
        this.cancelEditingNote();
        this.savingNoteId.set(null);
      },
      error: error => {
        console.error('Failed to save note:', error);
        this.errorMessage.set('Could not save note.');
        this.savingNoteId.set(null);
      },
    });
  }

  deleteNote(note: Note): void {
    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      width: '400px',
      maxWidth: '90vw',
      data: {
        itemType: 'note'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed !== true) {
        return;
      }

      this.noteApi.delete(this.documentId(), note.id).subscribe({
        next: () => {
          this.notes.update(notes =>
            notes.filter(existing => existing.id !== note.id)
          );

          if (this.editingNoteId() === note.id) {
            this.cancelEditingNote();
          }
        },

        error: error => {
          console.error('Failed to delete note:', error);
          this.errorMessage.set('Could not delete note.');
        }
      });
    });
  }

  startAddingNote(): void {
    this.newNoteContent = '';
    this.isAddingNote.set(true);
  }

  cancelAddingNote(): void {
    this.newNoteContent = '';
    this.isAddingNote.set(false);
  }

  saveNewNote(): void {
    if (
      this.isSavingNewNote() ||
      !this.newNoteContent.trim() ||
      this.newNoteContent.length > 500
    ) {
      return;
    }

    const content = this.newNoteContent.trim();
    this.isSavingNewNote.set(true);
    this.errorMessage.set('');
    this.noteApi.add(this.documentId(), content).subscribe({
      next: note => {
        this.notes.update(notes => [...notes, note]);
        this.cancelAddingNote();
        this.isSavingNewNote.set(false);
      },
      error: error => {
        console.error('Failed to add note:', error);
        this.errorMessage.set('Could not add note.');
        this.isSavingNewNote.set(false);
      },
    });
  }
}
