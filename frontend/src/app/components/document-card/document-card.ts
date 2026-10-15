import { Component, inject, input, output } from '@angular/core';
import { PaperlessDocument } from '../../models/paperless-document';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NoteModal } from '../note-modal/note-modal';
import { MatDialog } from '@angular/material/dialog';

import {MatTooltip} from '@angular/material/tooltip';
import {Router} from '@angular/router';
@Component({
  selector: 'app-document-card',
  imports: [MatCard, MatIcon, MatButtonModule, MatTooltip],
  templateUrl: './document-card.html',
  styleUrl: './document-card.css',
})
export class DocumentCard {

  readonly dialog = inject(MatDialog);
  private router = inject(Router);

  document = input.required<PaperlessDocument>();
  delete = output<string>();

  openDocument(): void {
    this.router.navigate(['/document-details', this.document().id]);
  }

  openNoteModal() {
    this.dialog.open(NoteModal, {
      width: '600px',
      maxHeight: '80vh',
      data: this.document(),
    });
  }

}
