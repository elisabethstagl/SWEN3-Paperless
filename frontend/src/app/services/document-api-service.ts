import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PaperlessDocument } from '../models/paperless-document';

@Injectable({
  providedIn: 'root',
})
export class DocumentApiService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/documents';

  getAll() {
    return this.http.get<PaperlessDocument[]>(this.baseUrl);
  }

  delete(id: string) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  upload(file: File) {
    const formData = new FormData();
    formData.append('file', file)

    return this.http.post<PaperlessDocument>(this.baseUrl, formData)

  }


}
