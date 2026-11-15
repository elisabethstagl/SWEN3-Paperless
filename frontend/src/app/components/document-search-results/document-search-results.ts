
import { Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import {
  catchError,
  distinctUntilChanged,
  map,
  of,
  startWith,
  switchMap
} from 'rxjs';

import { DocumentSearchService } from '../../services/document-search-service';

interface TextPart {
  text: string;
  highlighted: boolean;
}

interface SearchState {
  loading: boolean;
  error: boolean;
  snippets: string[];
}

@Component({
  selector: 'app-document-search-results',
  templateUrl: './document-search-results.html',
  styleUrl: './document-search-results.css',
})
export class DocumentSearchResults {
  readonly documentId = input.required<string>();

  private readonly route = inject(ActivatedRoute);
  private readonly searchService = inject(DocumentSearchService);

  private readonly query = toSignal(
    this.route.queryParamMap.pipe(
      map(params => (params.get('q') ?? '').trim()),
      distinctUntilChanged()
    ),
    { initialValue: this.route.snapshot.queryParamMap.get('q')?.trim() ?? '' }
  );

  readonly searchTerm = computed(() => this.query());

  readonly state = toSignal(
    this.route.queryParamMap.pipe(
      map(params => (params.get('q') ?? '').trim()),
      distinctUntilChanged(),
      switchMap(query => {
        if (!query) {
          return of<SearchState>({
            loading: false,
            error: false,
            snippets: []
          });
        }

        return this.searchService
          .searchInDocument(this.documentId(), query)
          .pipe(
            map(result => ({
              loading: false,
              error: false,
              snippets: result.snippets
            })),
            startWith({
              loading: true,
              error: false,
              snippets: []
            }),
            catchError(error => {
              console.error('Document search failed:', error);
              return of<SearchState>({
                loading: false,
                error: true,
                snippets: []
              });
            })
          );
      })
    ),
    {
      initialValue: {
        loading: false,
        error: false,
        snippets: []
      } as SearchState
    }
  );

  readonly snippets = computed(() => this.state().snippets);

  splitHighlight(snippet: string): TextPart[] {
    const parts = snippet.split(/(\[\[HIT\]\]|\[\[\/HIT\]\])/);
    const result: TextPart[] = [];
    let highlighted = false;

    for (const part of parts) {
      if (part === '[[HIT]]') {
        highlighted = true;
      } else if (part === '[[/HIT]]') {
        highlighted = false;
      } else if (part) {
        result.push({ text: part, highlighted });
      }
    }

    return result;
  }
}
