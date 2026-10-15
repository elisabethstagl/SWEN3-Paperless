import { Component, inject } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-document-details',
  imports: [],
  templateUrl: './document-details.html',
  styleUrl: './document-details.css',
})
export class DocumentDetails {

  private route = inject(ActivatedRoute);

  documentId = this.route.snapshot.paramMap.get('id');
}
