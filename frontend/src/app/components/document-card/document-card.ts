import {Component, inject, input, output} from '@angular/core';
import {PaperlessDocument} from '../../models/paperless-document';
import {MatCard} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';

import {MatTooltip} from '@angular/material/tooltip';
import {Router} from '@angular/router';
import {ConfirmDeleteDialog} from '../confirm-delete-dialog/confirm-delete-dialog';

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

  confirmDelete(): void {
    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      width: '400px',
      maxWidth: '90vw',
      data: {
        itemType: 'document'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed === true) {
        this.delete.emit(this.document().id);
      }
    });
  }

}
