import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentNotes } from './document-notes';

describe('DocumentNotes', () => {
  let component: DocumentNotes;
  let fixture: ComponentFixture<DocumentNotes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentNotes],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentNotes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
