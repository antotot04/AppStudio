import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CardItem } from '../dto/card-item';
import { StudyService } from '../service/study/study-service';
import { Router } from '@angular/router';
import { DeckDTO } from '../dto/deck-dto';
import { form, FormField } from '@angular/forms/signals';
import { SearchEntry } from '../dto/search-entry';
import { CardSettings } from "../card-settings/card-settings";

@Component({
  selector: 'app-deck-page',
  imports: [FormField, CardSettings],
  templateUrl: './deck-page.html',
  styleUrl: './deck-page.css',
})
export class DeckPage implements OnInit {

  private studyService = inject(StudyService);
  router = inject(Router);
  deckId = this.router.url.slice(this.router.url.indexOf('deck/') + 5);
  username = this.router.url.slice(1, this.router.url.indexOf('/', this.router.url.indexOf('/') + 1));
  deckInfo = signal<DeckDTO>({
    deckTitle: '',
    deckLayout: 'general'
  });

  popUpCardSettings = signal<boolean>(false);
  cardSettingsMode = signal<"edit" | "create">("create");
  currCardId = signal<string | undefined>(undefined);
  currCardLayout = signal<"quiz" | "double-sided" | "true-false">("double-sided");

  cardList: CardItem[] = [];
  cardsToDisplay = signal<CardItem[]>([]);
  cardsCounter = computed<number>(() => {
    return this.cardsToDisplay().length;
  });

  formModel = signal<SearchEntry>({
    layout: '',
    word: ''
  });

  searchForm = form(this.formModel);

  execSearch(){
    if(this.searchForm.layout().value() !== ''){
      this.cardsToDisplay.update(() => 
        this.cardList.filter((card) => 
          card.layout === this.searchForm.layout().value() &&
          card.front.includes(this.searchForm.word().value())
        )
      );
    }else{
      this.cardsToDisplay.update(() => 
        this.cardList.filter((card) => 
          card.front.includes(this.searchForm.word().value())
        )
      );
    }
  }

  onSubmit(event: Event){
    event.preventDefault();
    this.execSearch();
  }

  onNewCard(){
    this.popUpCardSettings.set(true);
    this.cardSettingsMode.set("create");
    this.currCardId.set(undefined);
    const currDeckLayout = this.deckInfo().deckLayout;
    if(currDeckLayout === "general"){
      this.currCardLayout.set("double-sided"); // default layout choice
    }else{
      this.currCardLayout.set(currDeckLayout);
    }
  }

  onEditCard(cardId: string, layout: "quiz" | "double-sided" | "true-false"){
    this.popUpCardSettings.set(true);
    this.cardSettingsMode.set("edit");
    this.currCardId.set(cardId);
    this.currCardLayout.set(layout);
  }

  onCardSettingsTerminated(isSuccessful: boolean){
    if(isSuccessful){
      this.getDeckCards();
    }
    this.popUpCardSettings.set(false);
  }

  onDeleteDeck(){
    if(confirm("Do you really want to delete this deck? (All your deck's data will be permanently deleted from your account)")){
      this.studyService.deleteUserDeck(this.deckId).subscribe({
        next: () => {
          this.router.navigate([this.username, 'study']);
        },
        error: () => {
          console.log("onDeleteDeck: error");
        }
      });
    }
  }

  onDeleteCard(cardId: string, layout: "quiz" | "double-sided" | "true-false"){
    if(confirm("Do you really want to delete this card?")){
      this.studyService.deleteDeckCard(cardId, layout).subscribe({
        next: () => {
          console.log("onDeleteCard: deleted")
          this.getDeckCards();
        },
        error: () => {
          console.log("onDeleteCard: error");
        }
      });
    }
  }

  getDeckInfo(){
    this.studyService.getUserDeck(this.deckId).subscribe({
      next: (resp) => {
        if(resp.layout === null){
          resp.layout = "general";
        }

        this.deckInfo.set({
          deckLayout: resp.layout,
          deckTitle: resp.title
        });
      },
      error: () => {
        console.log("getDeckInfo: error");
      }
    })
  }

  getDeckCards(){
    this.studyService.getDeckCards(this.deckId).subscribe({
      next: (resp) => {
        this.cardList = resp;
        this.cardsToDisplay.set(this.cardList);
        // TODO: on next commit
        // this.execSearch()
      },
      error: () => {
        console.log("getDeckCards: error");
      }
    })
  }

  ngOnInit(){
    this.getDeckInfo();
    this.getDeckCards();
  }
}
