import { Component } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatDivider} from '@angular/material/list';

@Component({
  selector: 'app-home',
  imports: [MatButtonModule, MatIconModule, MatDivider],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
