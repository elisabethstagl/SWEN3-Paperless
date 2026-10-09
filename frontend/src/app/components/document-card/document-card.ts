import { Component, input, output } from '@angular/core';
import { PaperlessDocument } from '../../models/paperless-document';
import { MatCard } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import {MatTooltip} from '@angular/material/tooltip';
@Component({
  selector: 'app-document-card',
  imports: [MatCard, MatIcon, MatButtonModule, MatTooltip],
  templateUrl: './document-card.html',
  styleUrl: './document-card.css',
})
export class DocumentCard {
   document = input.required<PaperlessDocument>();
   delete = output<string>();

}
