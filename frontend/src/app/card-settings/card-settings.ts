import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { StudyService } from '../service/study/study-service';
import { QuizCard } from '../dto/quiz-card';
import { TrueFalseCard } from '../dto/true-false-card';
import { DoubleSidedCard } from '../dto/double-sided-card';
import { form, maxLength, required, FormField } from '@angular/forms/signals';
import { QuizOption } from '../dto/quiz-option';
import { Router } from '@angular/router';
import { QuizOptionData } from '../dto/quiz-option-data';
import { QuizCardData } from '../dto/quiz-card-data';
import { forkJoin, Observable } from 'rxjs';

interface CardSettingsForm {
  front: string,
  back: string,
  validity: "true" | "false"
}

@Component({
  selector: 'app-card-settings',
  imports: [FormField],
  templateUrl: './card-settings.html',
  styleUrl: './card-settings.css',
})
export class CardSettings implements OnInit {
  cardService = inject(StudyService);
  router = inject(Router);
  deckId = this.router.url.slice(this.router.url.indexOf('deck/') + 5);
  /* component I/O */
  hasTerminated = output<boolean>();
  pageFunc = input<"edit" | "create">();
  cardId = input<string>();
  deckLayout = input<"double-sided" | "true-false" | "quiz" | "general">();
  layout = input<"double-sided" | "true-false" | "quiz">();
  /* current selected layout */
  layoutEdited = signal<"double-sided" | "true-false" | "quiz">("double-sided");
  /* list of options for quiz card */
  optionFormList = signal<QuizOption[]>([]);
  /* temporary id's of new options to register on db */
  tempOptIds = signal<String[]>([]);
  /* DTO to send to backend */
  prepareDTO = computed<QuizCard | QuizCardData | TrueFalseCard | DoubleSidedCard>(() => {
    switch(this.layoutEdited()){
      case "quiz":
      return {
        front: this.cardForm.front().value(),
        options: this.prepareOptions()
      }
      case "double-sided":
      return {
        front: this.cardForm.front().value(),
        back: this.cardForm.back().value()
      }
      default:
      return {
        front: this.cardForm.front().value(),
        validity: this.cardForm.validity().value() === "true" ? true : false
      }
    }
  });
  /* disable layout options in select */
  quizDisabled = true;
  doubleSidedDisabled = true;
  trueFalseDisabled = true;
  /* page title */
  cardPageTitle = computed<string>(() => {
    if(this.pageFunc() === undefined){
      throw new Error("page function is undefined");
    }
    if(this.layout() === undefined){
      throw new Error("layout is undefined");
    }

    const predicate = this.pageFunc() === "create" ? "NEW" : this.pageFunc()?.toUpperCase();
    const layout = this.layoutEdited().toUpperCase();

    return predicate + " " + layout + " CARD";
  })
  /* card infos */
  initCard: QuizCard | TrueFalseCard | DoubleSidedCard = {
    front: '',
    back: ''
  };
  card = signal<QuizCard | TrueFalseCard | DoubleSidedCard>({
    front: '',
    back: ''
  });

  /* form init */
  formModel = signal<CardSettingsForm>({
    front: '',
    back: '',
    validity: "true"
  });

  cardForm = form(this.formModel, (schemaPath) => {
    required(schemaPath.front);
    maxLength(schemaPath.front, 600);
    switch(this.layoutEdited()){
      case "double-sided":
        required(schemaPath.back);
        break;
      case "true-false":
        required(schemaPath.validity);
        break;
      default:
        break;
    }
  });

  onChangeLayout(event: Event){
    const input = event.target as HTMLInputElement;
    this.layoutEdited.set(input.value as "quiz" | "double-sided" | "true-false");
  }

  /* OPTION RELATED CALLS */

  addValidity(event: Event, index: string){
    const input = event.target as HTMLInputElement;
    this.optionFormList.update((list) => {
      list.forEach((option) => {
        if(option.idOption === index){
          option.validity = input.checked;
        }
      });
      return list;
    });
  }

  addAnswerText(event: Event, index: string){
    const input = event.target as HTMLInputElement;
    this.optionFormList.update((list) => {
      list.forEach((option) => {
        if(option.idOption === index){
          option.answerText = input.value;
        }
      });
      return list;
    });
  }

  onAddOption(){
    if(this.optionFormList().length >= 5){
      return;
    }

    this.optionFormList.update((list) => {
      const newEntry: QuizOption = {
        idOption: crypto.randomUUID(), // this id will be omitted on registration (used only to track option)
        answerText: '',
        validity: false
      }

      /* update option form list */
      list.push(newEntry);

      /* register id as temporary */
      this.tempOptIds.update((tempList) => {
        tempList.push(newEntry.idOption);
        return tempList;
      });

      return list;
    });
  }

  deleteShallowOption(optToDelete: QuizOption){
    this.optionFormList.update((list) => {
      return list.filter((opt) => opt.idOption !== optToDelete.idOption);
    });

    this.tempOptIds.update((list) => {
      return list.filter((id) => id !== optToDelete.idOption);
    })
  }

  deleteFullOption(opt: QuizOption){
    this.cardService.deleteQuizOption(this.cardId()!, opt.idOption).subscribe(() => {
      console.log("deleteRemoteOption: ok");
      this.deleteShallowOption(opt);
    });
  }

  onDeleteOption(index: string){
    if(!confirm("Do you really want to delete this option?")){
      return;
    }

    const option= this.optionFormList().find((opt) => opt.idOption === index);
    if(option === undefined){
      throw new Error("option index is undefined");
    }

    if(this.tempOptIds().find((id) => id === option.idOption) === undefined){
      this.deleteFullOption(option);
    }else{
      this.deleteShallowOption(option);
    }
  }

  /* Note on "edit" mode: new options are left out */
  prepareOptions(): QuizOption[] | QuizOptionData[]{
    if(this.pageFunc() === "create"){
      return this.optionFormList().map((opt) => {
        return {
          answerText: opt.answerText,
          validity: opt.validity
        } as QuizOptionData
      });
    }else if(this.pageFunc() === "edit"){
      // case update double-sided/true-false card to a quiz card
      if(this.layout() !== "quiz"){
        return this.optionFormList().map((opt) => {
          return {
            answerText: opt.answerText,
            validity: opt.validity
          } as QuizOptionData
        });
      }
      return this.optionFormList().filter((opt) => 
        !this.tempOptIds().find((id) => id === opt.idOption)
      );
    }else{
      throw new Error("pageFunc can't be null");
    }
  }

  registerNewOptions(){
    let registerOptionsList: Observable<Object>[] = [];
    const currCardId = this.cardId();
    if(currCardId === undefined){
      throw new Error("card id is undefined");
    }

    this.tempOptIds().forEach((id) => {
      this.optionFormList().forEach((opt) => {
        if(opt.idOption === id){
          registerOptionsList.push(this.cardService.registerOption(currCardId, opt));
        }
      });
    });

    forkJoin(registerOptionsList).subscribe({
      next: () => {
        this.hasTerminated.emit(true);
      }
    })
  }

  /* CARD RELATED CALLS */

  registerCard(data: QuizCardData | TrueFalseCard | DoubleSidedCard){
    this.cardService.reigsterCard(this.deckId, this.layoutEdited(), data).subscribe(() => {
      if(this.pageFunc() === "create"){
        this.hasTerminated.emit(true);
      }
    });
  }

  editCard(data: QuizCard | TrueFalseCard | DoubleSidedCard){
    this.cardService.updateCard(this.cardId()!, this.layoutEdited(), data).subscribe(() => {
      if(this.layoutEdited() === "quiz" && this.tempOptIds().length !== 0){
        this.registerNewOptions();
      }else{
        this.hasTerminated.emit(true);
      }
    });
  }

  deleteOldLayoutInstance(){
    const oldCardId = this.cardId();
    if(oldCardId === undefined){
      throw new Error("card id is undefined");
    }
    const oldCardLayout = this.layout();
    if(oldCardLayout === undefined){
      throw new Error("initial layout is undefined");
    }
    this.cardService.deleteDeckCard(oldCardId, oldCardLayout).subscribe(() => {
      console.log("deleteOldLayoutInstance: ok");
      this.hasTerminated.emit(true);
    })
  }

  onSubmit(event: Event){
    event.preventDefault();

    const data: QuizCard | QuizCardData | TrueFalseCard | DoubleSidedCard = this.prepareDTO();

    if(this.pageFunc() === "create"){
      this.registerCard(data as QuizCardData | TrueFalseCard | DoubleSidedCard);
    }else if(this.pageFunc() === "edit"){
      if(this.layout() !== this.layoutEdited()){
        // case change card layout
        this.registerCard(data as QuizCardData | TrueFalseCard | DoubleSidedCard);
        this.deleteOldLayoutInstance();
      }else{
        // quiz card --> update already registered options --> register new options
        this.editCard(data as QuizCard | TrueFalseCard | DoubleSidedCard);
      }
    }else{
      throw new Error("pageFunc can't be undefined");
    }
  }

  listOptComp(initList: QuizOption[], editedList: QuizOption[]): boolean{
    if(initList.length !== editedList.length){ 
      return false;
    }

    for(let i = 0; i < initList.length; i++){
      if(initList[i].answerText !== editedList[i].answerText || initList[i].validity !== editedList[i].validity){
        return false;
      }
    }

    return true;
  }

  onExit(){
    let editedInfos = false;
    let card: QuizCard | DoubleSidedCard | TrueFalseCard = this.initCard;

    switch(this.layoutEdited()){
      case "quiz":
        card = this.initCard as QuizCard;
        if(this.pageFunc() === "edit"){
          if(card.front !== this.cardForm.front().value() || 
          !this.listOptComp(card.options, this.optionFormList())){
            editedInfos = true;
          }
        }else{
          if(this.cardForm.front().value() !== '' ||
          this.optionFormList().filter((opt) => 
            opt.answerText !== '' ||
            opt.validity !== false
          ).length !== 0
          ){
            editedInfos = true;
          }
        }
        break;
      case "double-sided":
        card = this.initCard as DoubleSidedCard;
        if(this.pageFunc() === "edit"){
          if(card.front !== this.cardForm.front().value() || 
          card.back !== this.cardForm.back().value()){
            editedInfos = true;
          }
        }else{
          if(this.cardForm.front().value() !== '' ||
          this.cardForm.back().value() !== ''){
            editedInfos = true;
          }
        }
        break;
      default:
        card = this.initCard as TrueFalseCard;
        if(this.pageFunc() === "edit"){
          let validity = false;
          if(this.cardForm.validity().value() === "true"){
            validity = true;
          }
          if(card.front !== this.cardForm.front().value() || 
          card.validity !== validity){
            editedInfos = true;
          }
        }else{
          if(this.cardForm.front().value() !== '' ||
          this.cardForm.validity().value() !== "true"){
            editedInfos = true;
          }
        }
        break;
    }

    if(!editedInfos){
      this.hasTerminated.emit(true);
      return;
    }
    
    if(confirm("Card informations hasn't been saved. Do you still want to exit the page?")){
      this.hasTerminated.emit(false);
    }
  }

  setDisabledLayouts(){
    switch(this.deckLayout()){
      case "double-sided":
        this.doubleSidedDisabled = false;
        break;
      case "true-false":
        this.trueFalseDisabled = false;
        break;
      case "quiz":
        this.quizDisabled = false;
        break;
      case "general":
        this.doubleSidedDisabled = false;
        this.trueFalseDisabled = false;
        this.quizDisabled = false;
        break;
      default:
        throw new Error("deck layout is undefined");
    }
  }

  loadCard(){
    const thisId = this.cardId();
    const thisLayout = this.layout();
    console.log(thisId);
    console.log(thisLayout);
    if(thisId !== undefined && thisLayout !== undefined){
      this.cardService.getDeckCard(thisId, thisLayout).subscribe((resp) => {
        this.initCard = resp;
        this.card.set(this.initCard);

        // form init here
        switch(thisLayout){
          case "double-sided":
            this.formModel.set({
              front: (resp as DoubleSidedCard).front,
              back: (resp as DoubleSidedCard).back,
              validity: "true"
            });
            break;
          case "true-false":
            this.formModel.set({
              front: (resp as TrueFalseCard).front,
              back: '',
              validity: (resp as TrueFalseCard).validity ? "true" : "false"
            });
            break;
          default:
            this.formModel.set({
              front: (resp as QuizCard).front,
              back: '',
              validity: "true"
            });
            this.optionFormList.set((resp as QuizCard).options);
            break;
        }
      })
    }
  }

  ngOnInit(){
    this.loadCard();

    const currLayout = this.layout();
    if(currLayout !== undefined){
      this.layoutEdited.set(currLayout);
    }

    this.setDisabledLayouts();
  }
}