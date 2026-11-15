
import { Component, computed, inject, signal } from '@angular/core';
import {
  Router,
  RouterLink,
  NavigationEnd
} from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';

import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
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

  readonly searchQuery = signal('');

  private readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url)
    ),
    { initialValue: this.router.url }
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
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      const tree = this.router.parseUrl(this.router.url);
      this.searchQuery.set(tree.queryParams['q'] ?? '');
    });

    const tree = this.router.parseUrl(this.router.url);
    this.searchQuery.set(tree.queryParams['q'] ?? '');
  }

  search(query: string): void {
    const trimmedQuery = query.trim();
    this.searchQuery.set(trimmedQuery);

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
