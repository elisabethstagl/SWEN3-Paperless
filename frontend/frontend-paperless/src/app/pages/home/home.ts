import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {MatSlideToggle} from '@angular/material/slide-toggle';

@Component({
  selector: 'app-home',
  imports: [RouterLink, MatSlideToggle ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
