import {Note} from './paperless-note';

export interface PaperlessDocument {
  id: string;
  filename: string;
  creator: string | null;
  author: string | null;
  summary: string | null;
  pages: number | null;
  createdOn: string | null;
  notes: Note[];
}
