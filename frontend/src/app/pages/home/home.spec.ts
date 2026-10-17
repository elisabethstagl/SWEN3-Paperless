import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Home } from './home';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows an uploaded document in the list', async () => {
    httpMock.expectOne('/api/documents').flush([]);

    component.documentStore.upload(new File([''], 'invoice.pdf'));
    httpMock.expectOne({ method: 'POST', url: '/api/documents' }).flush({ id: '1', filename: 'invoice.pdf', notes: [] });
    await fixture.whenStable();

    expect(fixture.nativeElement.textContent).toContain('invoice.pdf');
  });
});
