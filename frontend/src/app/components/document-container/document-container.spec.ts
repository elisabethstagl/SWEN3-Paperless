import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentContainer } from './document-container';

describe('DocumentContainer', () => {
  let component: DocumentContainer;
  let fixture: ComponentFixture<DocumentContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentContainer],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentContainer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
