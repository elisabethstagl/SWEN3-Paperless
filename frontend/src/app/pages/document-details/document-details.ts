
import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';

import { Header } from '../../components/header/header';
import { PaperlessDocument } from '../../models/paperless-document';
import { Note } from '../../models/paperless-note';
import { DocumentApiService } from '../../services/document-api-service';
import { DocumentStore } from '../../services/document-store';
import { NoteApiService } from '../../services/note-api-service';

@Component({
  selector: 'app-document-details',
  imports: [
    Header,
    DatePipe,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTooltipModule,
  ],
  templateUrl: './document-details.html',
  styleUrl: './document-details.css',
})
export class DocumentDetails implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly documentApi = inject(DocumentApiService);
  private readonly documentStore = inject(DocumentStore);
  private readonly noteApi = inject(NoteApiService);
  private readonly fb = inject(FormBuilder);

  // DOCUMENT STATE

  readonly document = signal<PaperlessDocument | null>(null);
  readonly isLoading = signal(true);
  readonly isEditing = signal(false);
  readonly isSaving = signal(false);

  // NOTES STATE

  readonly notes = signal<Note[]>([]);
  readonly editingNoteId = signal<string | null>(null);
  readonly savingNoteId = signal<string | null>(null);

  readonly isAddingNote = signal(false);
  readonly isSavingNewNote = signal(false);

  noteContent = '';
  newNoteContent = '';

  // ERROR STATE

  readonly errorMessage = signal('');

  // Only these fields can be edited

  readonly form = this.fb.nonNullable.group({
    filename: [''],
    author: [''],
    creator: [''],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.loadDocument(id);
      this.loadNotes(id);
    } else {
      this.errorMessage.set('No document ID provided.');
      this.isLoading.set(false);
    }
  }

  // LOAD DOCUMENT

  loadDocument(id: string): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.documentApi.getById(id).subscribe({
      next: document => {
        this.document.set(document);
        this.resetForm();
        this.isLoading.set(false);
      },
      error: error => {
        console.error('Failed to load document:', error);
        this.errorMessage.set('Could not load document.');
        this.isLoading.set(false);
      },
    });
  }

  // LOAD NOTES

  loadNotes(documentId: string): void {
    this.noteApi.getAll(documentId).subscribe({
      next: notes => {
        this.notes.set(notes);
      },
      error: error => {
        console.error('Failed to load notes:', error);
        this.errorMessage.set('Could not load notes.');
      },
    });
  }

  // DOCUMENT EDITING

  resetForm(): void {
    const document = this.document();

    if (!document) return;

    this.form.setValue({
      filename: document.filename,
      author: document.author ?? '',
      creator: document.creator ?? '',
    });
  }

  startEditing(): void {
    this.isEditing.set(true);
  }

  cancelEditing(): void {
    this.resetForm();
    this.isEditing.set(false);
  }

  saveChanges(): void {
    const document = this.document();

    if (!document || this.isSaving()) return;

    this.isSaving.set(true);
    this.errorMessage.set('');

    const values = this.form.getRawValue();

    const updatedDocument: PaperlessDocument = {
      ...document,
      filename: values.filename,
      author: values.author || null,
      creator: values.creator || null,
    };

    this.documentApi.update(document.id, updatedDocument).subscribe({
      next: savedDocument => {
        this.document.set(savedDocument);
        this.documentStore.updateDocument(savedDocument);

        this.resetForm();
        this.isEditing.set(false);
        this.isSaving.set(false);
      },
      error: error => {
        console.error('Failed to save document:', error);
        this.errorMessage.set('Could not save document.');
        this.isSaving.set(false);
      },
    });
  }

  // EDIT NOTE

  startEditingNote(note: Note): void {
    this.editingNoteId.set(note.id);
    this.noteContent = note.content;
  }

  cancelEditingNote(): void {
    this.editingNoteId.set(null);
    this.noteContent = '';
  }

  saveNote(note: Note): void {
    const document = this.document();
    const content = this.noteContent.trim();

    if (
      !document ||
      this.savingNoteId() ||
      !content ||
      content.length > 500
    ) return;

    this.savingNoteId.set(note.id);
    this.errorMessage.set('');

    this.noteApi.update(document.id, note.id, content).subscribe({
      next: updatedNote => {
        this.notes.update(notes =>
          notes.map(existing =>
            existing.id === note.id ? updatedNote : existing
          )
        );

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

  // DELETE NOTE

  deleteNote(note: Note): void {
    const document = this.document();

    if (!document) return;

    this.noteApi.delete(document.id, note.id).subscribe({
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
      },
    });
  }

  // ADD NOTE

  startAddingNote(): void {
    this.newNoteContent = '';
    this.isAddingNote.set(true);
  }

  cancelAddingNote(): void {
    this.newNoteContent = '';
    this.isAddingNote.set(false);
  }

  saveNewNote(): void {
    const document = this.document();
    const content = this.newNoteContent.trim();

    if (
      !document ||
      !content ||
      content.length > 500 ||
      this.isSavingNewNote()
    ) return;

    this.isSavingNewNote.set(true);
    this.errorMessage.set('');

    this.noteApi.add(document.id, content).subscribe({
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
