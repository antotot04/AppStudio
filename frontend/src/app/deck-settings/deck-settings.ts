import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { StudyService } from '../service/study/study-service';
import { DeckDTO } from '../dto/deck-dto';
import { form, required, FormField, maxLength } from '@angular/forms/signals';
import { Router } from '@angular/router';

@Component({
  selector: 'app-deck-settings',
  imports: [FormField],
  templateUrl: './deck-settings.html',
  styleUrl: './deck-settings.css',
})
export class DeckSettings implements OnInit {
  hasTerminated = output<boolean>();
  private studyService = inject(StudyService);
  private router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1));

  // page state: 'create' | 'edit' a deck
  pageFunc = input<'create' | 'edit'>();
  deckId = input<string>();

  // initial deck infos
  initTitle = signal('');
  initLayout = signal('general');

  // disabled options
  quizDisabled = signal(false);
  trueFalseDisabled = signal(false);
  doubleSidedDisabled = signal(false);

  onNewDeck = computed<boolean>(() => {
    if(this.pageFunc() === 'create'){
      return true;
    }else{
      if(this.pageFunc() === undefined){
        console.log("pageFunc is undefined");
      }
      return false;
    }
  });

  formModel = signal<DeckDTO>({
    deckTitle: '',
    deckLayout: 'general'
  });

  deckForm = form(this.formModel, (schemaPath) => {
    maxLength(schemaPath.deckTitle, 50);
    required(schemaPath.deckTitle);
  });

  createUserDeck(deckData: DeckDTO){
    this.studyService.registerUserDeck(this.username, deckData).subscribe(() => {
      this.hasTerminated.emit(true);
    })
  }

  editUserDeck(deckData: DeckDTO){
    if(this.deckId() !== undefined){
      this.studyService.updateUserDeck(this.deckId()!, deckData).subscribe(() => {
        this.hasTerminated.emit(true);
      });
    }
  }

  onSubmit(event: Event){
    event.preventDefault();

    const deckData: DeckDTO = {
      deckTitle: this.deckForm.deckTitle().value(),
      deckLayout: this.deckForm.deckLayout().value()
    };

    if(this.pageFunc() === "create"){
      this.createUserDeck(deckData);
    }else if(this.pageFunc() === "edit"){
      if(deckData.deckTitle === this.initTitle() &&
      deckData.deckLayout === this.initLayout()){
        this.hasTerminated.emit(false);
        return; 
      }

      this.editUserDeck(deckData);
    }
  }

  onExit(){
    if(this.deckForm.deckTitle().value() !== this.initTitle() || 
      this.deckForm.deckLayout().value() !== this.initLayout()){
      if(confirm("If you exit your changes won't be saved")){
        this.hasTerminated.emit(false);
      }
    }else{
      this.hasTerminated.emit(false);
    }
  }

  getDeckInfo(){
    if(this.deckId() !== undefined){
      this.studyService.getUserDeck(this.deckId()!).subscribe((resp) => {
        this.formModel.set({
          deckTitle: resp.title,
          deckLayout: resp.layout
        });
        this.initTitle.set(resp.title);
        this.initLayout.set(resp.layout);
      })
    }
  }

  setDisabledOptions(){
    if(this.deckId() !== undefined){
      this.studyService.getDeckCards(this.deckId()!).subscribe((resp) => {
        if(resp.length === 0) /* no cards in deck -> can be any layout */
          return;
          
        const possibleLayout = resp[0].layout;
        for(const card of resp){
          if(card.layout !== possibleLayout){
            this.doubleSidedDisabled.set(true);
            this.trueFalseDisabled.set(true);
            this.quizDisabled.set(true);
            break;
          }
        }
        
        if(!this.doubleSidedDisabled() && 
        !this.trueFalseDisabled() && 
        !this.quizDisabled()){
          if(possibleLayout == "quiz"){
            this.trueFalseDisabled.set(true);
            this.doubleSidedDisabled.set(true);
          }else if(possibleLayout == "double-sided"){
            this.trueFalseDisabled.set(true);
            this.quizDisabled.set(true);
          }else{
            this.doubleSidedDisabled.set(true);
            this.quizDisabled.set(true);
          }
        }
      })
    }
  }

  ngOnInit(){
    this.getDeckInfo();

    /* checking for available layouts for selected deck */
    if(this.pageFunc() === "edit"){
      this.setDisabledOptions();
    }
  }
}
