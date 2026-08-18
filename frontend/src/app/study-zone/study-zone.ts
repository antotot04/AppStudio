import { Component, inject, OnInit, signal } from '@angular/core';
import { StudyService } from '../service/study/study-service';
import { Router } from '@angular/router';
import { DeckItem } from '../dto/deck-item';
import { DeckSettings } from "../deck-settings/deck-settings";

@Component({
  selector: 'app-study-zone',
  imports: [DeckSettings],
  templateUrl: './study-zone.html',
  styleUrl: './study-zone.css',
})
export class StudyZone implements OnInit {
  private studyService = inject(StudyService);
  private router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1));
  deckList = signal<DeckItem[]>([]);
  deckPopUp = signal(false);

  getAllUserDecks(){
    this.studyService.getAllDecks(this.username).subscribe({
      next: (resp) => {
        this.deckList.set(resp);
      },
      error: () => {
        console.log("getAllUserDecks: error");
      }
    })
  }

  onNewDeck(){
    this.deckPopUp.set(true);
  }

  onClose(){
    this.deckPopUp.set(false);
  }

  ngOnInit(){
    this.getAllUserDecks();
  }
}
