import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { DocumentDetails } from './document-details';

describe('DocumentDetails', () => {
  it('loads the document from the route id and shows it', async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([{ path: 'document-details/:id', component: DocumentDetails }]),
      ],
    });

    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/document-details/1', DocumentDetails);

    TestBed.inject(HttpTestingController).expectOne('/api/documents/1').flush({ id: '1', filename: 'invoice.pdf', notes: [] });
    await harness.fixture.whenStable();

    expect(harness.routeNativeElement?.textContent).toContain('invoice.pdf');
  });
});
