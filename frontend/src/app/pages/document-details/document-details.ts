import { Component, inject } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Header} from '../../components/header/header';

@Component({
  selector: 'app-document-details',
  imports: [
    Header
  ],
  templateUrl: './document-details.html',
  styleUrl: './document-details.css',
})
export class DocumentDetails {

  private route = inject(ActivatedRoute);

  documentId = this.route.snapshot.paramMap.get('id');
}
