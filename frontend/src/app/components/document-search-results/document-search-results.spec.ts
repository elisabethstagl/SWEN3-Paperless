
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { DocumentSearchResults } from './document-search-results';
import { DocumentSearchService } from '../../services/document-search-service';

describe('DocumentSearchResults', () => {
  let component: DocumentSearchResults;
  let fixture: ComponentFixture<DocumentSearchResults>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentSearchResults],
      providers: [
        provideRouter([]),
        {
          provide: DocumentSearchService,
          useValue: {
            searchInDocument: () => of({
              documentId: 'test-document-id',
              snippets: []
            })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentSearchResults);

    fixture.componentRef.setInput(
      'documentId',
      'test-document-id'
    );

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
