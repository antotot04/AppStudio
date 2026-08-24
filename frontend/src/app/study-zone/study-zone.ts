import { Component, inject, OnInit, signal } from '@angular/core';
import { StudyService } from '../service/study/study-service';
import { Router } from '@angular/router';
import { DeckItem } from '../dto/deck-item';
import { DeckSettings } from "../deck-settings/deck-settings";
import { form, FormField } from '@angular/forms/signals';

interface searchEntry {
  layout: "quiz" | "true-false" | "double-sided" | "general" | "",
  word: string
}

@Component({
  selector: 'app-study-zone',
  imports: [DeckSettings, FormField],
  templateUrl: './study-zone.html',
  styleUrl: './study-zone.css',
})
export class StudyZone implements OnInit {
  private studyService = inject(StudyService);
  private router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1));
  /* all fetched user decks */
  deckList: DeckItem[] = [];
  /* decks actually displayed based on search parameters */
  decksToDisplay = signal<DeckItem[]>([]);
  deckPopUp = signal(false);
  pageFunc = signal<'create' | 'edit'>('create');
  deckId = signal<undefined | string>(undefined);

  formModel = signal<searchEntry>({
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
        this.deckList = resp;
        this.deckList.forEach((deck) => {
          if(deck.layout === null) 
            deck.layout = "general";
        });
        this.decksToDisplay.set(this.deckList);
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

  onClose(deckCreated: boolean){
    this.deckPopUp.set(false);
    this.getAllUserDecks(); // refresh deck state
  }

  onStudy(){
    // TODO: redirect to study page
  }

  onOpen(){
    // TODO: redirect to deck page
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
