
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

export interface DocumentSearchResult {
  id: string;
  title: string;
  content: string;
  fileName: string;
  createdAt: string;
}

export interface DocumentHighlightResult {
  documentId: string;
  snippets: string[];
}


@Injectable({
  providedIn: 'root',
})

export class DocumentSearchService {

  private readonly http = inject(HttpClient);

  search(query: string) {
    return this.http.get<DocumentSearchResult[]>(
      '/api/elasticsearch/search',
      {
        params: { q: query }
      }
    );
  }

  searchInDocument(documentId: string, query: string) {
    return this.http.get<DocumentHighlightResult>(
      `/api/elasticsearch/documents/${encodeURIComponent(documentId)}/search`,
      { params: { q: query } }
    );
  }

}
