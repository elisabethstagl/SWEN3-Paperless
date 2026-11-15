import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { DocumentContainer } from './document-container';
import { provideRouter } from "@angular/router";

describe('DocumentContainer', () => {
  let component: DocumentContainer;
  let fixture: ComponentFixture<DocumentContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentContainer],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentContainer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
