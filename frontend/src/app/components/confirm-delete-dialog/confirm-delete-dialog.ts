import {Component, inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule
} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-confirm-delete-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './confirm-delete-dialog.html'
})
export class ConfirmDeleteDialog {
  readonly data = inject<{ itemType: string }>(MAT_DIALOG_DATA);
}
