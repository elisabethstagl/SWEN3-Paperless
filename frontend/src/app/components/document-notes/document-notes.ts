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
import {NotificationService} from '../../services/notification-service';
import {HttpErrorResponse} from '@angular/common/http';


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

  private readonly dialog = inject(MatDialog);
  private readonly notificationService = inject(NotificationService);

  noteContent = '';
  newNoteContent = '';

  ngOnInit(): void {
    this.loadNotes();
  }

  loadNotes(): void {
    this.noteApi.getAll(this.documentId()).subscribe({
      next: notes => {
        this.notes.set(notes);
      },

      error: (error: HttpErrorResponse) => {
        console.error('Failed to load notes:', error);

        this.notificationService.showHttpError(
          error,
          'Could not load notes'
        );
      }
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

    this.noteApi.update(this.documentId(), note.id, content).subscribe({
      next: updatedNote => {
        this.notes.update(notes =>
          notes.map(existing =>
            existing.id === note.id ? updatedNote : existing
          )
        );

        this.cancelEditingNote();
        this.savingNoteId.set(null);

        this.notificationService.showSuccess(
          'Note updated successfully'
        );
      },

      error: (error: HttpErrorResponse) => {
        console.error('Failed to save note:', error);
        this.savingNoteId.set(null);

        this.notificationService.showHttpError(
          error,
          'Could not save note'
        );
      }
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

          this.notificationService.showSuccess(
            'Note deleted successfully'
          );
        },

        error: (error: HttpErrorResponse) => {
          console.error('Failed to delete note:', error);

          this.notificationService.showHttpError(
            error,
            'Could not delete note'
          );
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

    this.noteApi.add(this.documentId(), content).subscribe({
      next: note => {
        this.notes.update(notes => [...notes, note]);
        this.cancelAddingNote();
        this.isSavingNewNote.set(false);

        this.notificationService.showSuccess(
          'Note added successfully'
        );
      },

      error: (error: HttpErrorResponse) => {
        console.error('Failed to add note:', error);
        this.isSavingNewNote.set(false);

        this.notificationService.showHttpError(
          error,
          'Could not add note'
        );
      }
    });
  }
}
