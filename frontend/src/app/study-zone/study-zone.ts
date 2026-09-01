import { Component, inject, OnInit, signal } from '@angular/core';
import { StudyService } from '../service/study/study-service';
import { Router, RouterLink } from '@angular/router';
import { DeckItem } from '../dto/deck-item';
import { DeckSettings } from "../deck-settings/deck-settings";
import { form, FormField } from '@angular/forms/signals';
import { SearchEntry } from '../dto/search-entry';
import { forkJoin, map} from 'rxjs';

interface DisplayedDeckItem extends DeckItem {
  learnDisabled?: boolean
}

@Component({
  selector: 'app-study-zone',
  imports: [DeckSettings, FormField, RouterLink],
  templateUrl: './study-zone.html',
  styleUrl: './study-zone.css',
})
export class StudyZone implements OnInit {
  private studyService = inject(StudyService);
  private router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1));
  /* all fetched user decks */
  deckList: DisplayedDeckItem[] = [];
  /* decks actually displayed based on search parameters */
  decksToDisplay = signal<DisplayedDeckItem[]>([]);
  deckPopUp = signal(false);
  pageFunc = signal<'create' | 'edit'>('create');
  deckId = signal<undefined | string>(undefined);

  formModel = signal<SearchEntry>({
    layout: '',
    word: ''
  });

  searchForm = form(this.formModel);

  execSearch(){
    if(this.searchForm.layout().value() !== ''){
      this.decksToDisplay.update(() => 
        this.deckList.filter((deck) => 
          deck.layout === this.searchForm.layout().value() &&
          deck.title.includes(this.searchForm.word().value())
        )
      );
    }else{
      this.decksToDisplay.update(() => 
        this.deckList.filter((deck) => 
          deck.title.includes(this.searchForm.word().value())
        )
      );
    }
  }

  getAllUserDecks(){
    this.studyService.getAllDecks(this.username).subscribe({
      next: (resp) => {
        const serviceList = resp.map((deck) =>
          this.studyService.getDeckCards(deck.id).pipe(map((list) => {
            return {
              id: deck.id,
              layout: deck.layout === null ? "general" : deck.layout,
              title: deck.title,
              learnDisabled: list.length === 0
            } as DisplayedDeckItem
          }))
        );
        forkJoin(serviceList).subscribe((realResp) => {
          this.deckList = realResp;
          this.decksToDisplay.set(this.deckList);
        })
      },
      error: () => {
        console.log("getAllUserDecks: error");
      }
    })
  }

  onSubmit(event: Event){
    event.preventDefault(); 
    this.execSearch();
  }

  onNewDeck(){
    this.deckPopUp.set(true);
    this.pageFunc.set('create');
    this.deckId.set(undefined);
  }

  onClose(updateState: boolean){
    this.deckPopUp.set(false);
    if(updateState){
      this.getAllUserDecks(); // refresh deck state
    }
  }

  onEdit(deckId: string){
    this.deckPopUp.set(true);
    this.pageFunc.set('edit');
    this.deckId.set(deckId);
  }

  ngOnInit(){
    this.getAllUserDecks();
  }
}
