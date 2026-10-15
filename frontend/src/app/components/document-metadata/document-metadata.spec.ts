import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentMetadata } from './document-metadata';

describe('DocumentMetadata', () => {
  let component: DocumentMetadata;
  let fixture: ComponentFixture<DocumentMetadata>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentMetadata],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentMetadata);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
