import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { DocumentStore } from './document-store';

describe('DocumentStore', () => {
  let service: DocumentStore;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(DocumentStore);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('loads all documents on creation', () => {
    httpMock.expectOne('/api/documents').flush([{ id: '1', filename: 'invoice.pdf', notes: [] }]);

    expect(service.documents().map(d => d.id)).toEqual(['1']);
  });
});
