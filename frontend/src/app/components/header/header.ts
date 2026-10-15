import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {MatDivider} from '@angular/material/list';

@Component({
  selector: 'app-header',
  imports: [RouterLink, MatDivider],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {}
