import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PaperlessDocument } from '../models/paperless-document';
import { Note } from '../models/paperless-note';

@Injectable({
  providedIn: 'root',
})
export class NoteApiService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/documents';


  getAll(documentId: string) {
    return this.http.get<Note[]>(this.baseUrl + '/' + documentId + '/notes');
  }

  add(documentId: string, content: string) {
    return this.http.post<Note>(this.baseUrl + '/' + documentId + '/notes', { content });
  }

  update(documentId: string, noteId: string, content: string) {
    return this.http.put<Note>(this.baseUrl + '/' + documentId + '/notes/' + noteId, { content });
  }

  delete(documentId: string, noteId: string) {
    return this.http.delete<void>(this.baseUrl + '/' + documentId + '/notes/' + noteId);
  }


}
