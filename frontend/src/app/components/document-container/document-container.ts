
import {
  Component,
  computed,
  inject,
  signal
} from '@angular/core';

import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, distinctUntilChanged, switchMap, of, catchError, tap } from 'rxjs';

import { DocumentCard } from '../document-card/document-card';
import { DocumentStore } from '../../services/document-store';
import { DocumentSearchService } from '../../services/document-search-service';

@Component({
  selector: 'app-document-container',
  imports: [DocumentCard],
  templateUrl: './document-container.html',
  styleUrl: './document-container.css',
})
export class DocumentContainer {
  readonly documentStore = inject(DocumentStore);

  private readonly searchService = inject(DocumentSearchService);
  private readonly route = inject(ActivatedRoute);

  readonly isSearching = signal(false);
  readonly searchError = signal(false);

  private readonly searchState = toSignal(
    this.route.queryParamMap.pipe(
      map(params => (params.get('q') ?? '').trim()),
      distinctUntilChanged(),
      tap(query => {
        this.isSearching.set(!!query);
        this.searchError.set(false);
      }),
      switchMap(query => {
        if (!query) {
          return of({
            ids: null as string[] | null,
            failed: false
          });
        }

        return this.searchService.search(query).pipe(
          map(results => ({
            ids: results.map(result => result.id) as string[] | null,
            failed: false
          })),
          catchError(error => {
            console.error('Elasticsearch search failed:', error);
            return of({
              ids: null as string[] | null,
              failed: true
            });
          })
        );
      }),
      tap(state => {
        this.isSearching.set(false);
        this.searchError.set(state.failed);
      })
    ),
    { initialValue: { ids: null, failed: false } }
  );

  readonly visibleDocuments = computed(() => {
    const documents = this.documentStore.documents();
    const state = this.searchState();

    if (state.failed) {
      return [];
    }

    if (state.ids === null) {
      return documents;
    }

    return documents.filter(doc => state.ids!.includes(doc.id));
  });
}
