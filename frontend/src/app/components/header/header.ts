
import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import {
  Router,
  RouterLink,
  NavigationEnd
} from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { debounce, form, FormField } from '@angular/forms/signals';
import { filter, map, startWith } from 'rxjs';

import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    FormField,
    MatDivider,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  private readonly router = inject(Router);

  private readonly searchQuery = signal('');

  readonly searchForm = form(this.searchQuery, path => {
    debounce(path, 300);
  });

  private readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url)
    ),
    { initialValue: this.router.url }
  );

  private readonly urlQuery = computed(() =>
    this.router.parseUrl(this.currentUrl()).queryParams['q'] ?? ''
  );

  readonly isDocumentDetails = computed(() =>
    this.currentUrl().startsWith('/document-details/')
  );

  readonly placeholder = computed(() =>
    this.isDocumentDetails()
      ? 'Search in this document...'
      : 'Search all documents...'
  );

  constructor() {
    effect(() => {
      const urlQuery = this.urlQuery();
      untracked(() => {
        if (this.searchQuery().trim() !== urlQuery) {
          this.searchQuery.set(urlQuery);
        }
      });
    });

    effect(() => {
      const query = this.searchForm().value().trim();
      untracked(() => {
        if (query !== this.urlQuery()) {
          this.search(query);
        }
      });
    });
  }

  search(query: string): void {
    const trimmedQuery = query.trim();

    if (this.isDocumentDetails()) {
      void this.router.navigate([], {
        queryParams: trimmedQuery ? { q: trimmedQuery } : {},
        queryParamsHandling: 'replace'
      });
    } else {
      void this.router.navigate(['/'], {
        queryParams: trimmedQuery ? { q: trimmedQuery } : {}
      });
    }
  }
}
