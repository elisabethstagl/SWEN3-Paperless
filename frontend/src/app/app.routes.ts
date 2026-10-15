import { Routes } from '@angular/router';
import {Home} from './pages/home/home';
import {DocumentDetails} from './pages/document-details/document-details';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'document-details/:id', component: DocumentDetails },
  { path: '**', redirectTo: '' },
];

