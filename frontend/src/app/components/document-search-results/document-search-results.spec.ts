import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentSearchResults } from './document-search-results';

describe('DocumentSearchResults', () => {
  let component: DocumentSearchResults;
  let fixture: ComponentFixture<DocumentSearchResults>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentSearchResults],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentSearchResults);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
