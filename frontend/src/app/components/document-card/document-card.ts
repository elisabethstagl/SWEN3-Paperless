import { Component, inject, input, output } from '@angular/core';
import { PaperlessDocument } from '../../models/paperless-document';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NoteModal } from '../../note-modal/note-modal';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-document-card',
  imports: [MatCard, MatIcon, MatButtonModule],
  templateUrl: './document-card.html',
  styleUrl: './document-card.css',
})
export class DocumentCard {

  readonly dialog = inject(MatDialog);

  openNoteModal() {
    this.dialog.open(NoteModal, {
      width: '600px',
      maxHeight: '80vh',
      data: this.document(),
    });
  }
  document = input.required<PaperlessDocument>();
  delete = output<string>();

}
