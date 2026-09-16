import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CardItem } from '../dto/card-item';
import { StudyService } from '../service/study/study-service';
import { Router } from '@angular/router';
import { DoubleSidedCard } from '../dto/double-sided-card';
import { TrueFalseCard } from '../dto/true-false-card';
import { QuizCard } from '../dto/quiz-card';
import { form, required, FormField } from '@angular/forms/signals';

interface GeneralCard {
  doubleSided: DoubleSidedCard,
  trueFalse: TrueFalseCard,
  quiz: QuizCard
}

@Component({
  selector: 'app-study-deck',
  imports: [FormField],
  templateUrl: './study-deck.html',
  styleUrl: './study-deck.css',
})
export class StudyDeck implements OnInit {
  cardService = inject(StudyService);
  router = inject(Router);
  deckId = this.router.url.slice(this.router.url.indexOf('learn/')+6);
  username = this.router.url.slice(1, this.router.url.indexOf('/', this.router.url.indexOf('/') + 1));
  /* card state */
  currCardCounter = signal(-1);
  cardList: CardItem[] = [];
  currCard = signal<CardItem>({
    cardId: '',
    front: '',
    layout: 'double-sided'
  });
  /* card solutions */
  currCardInfo = signal<GeneralCard>({
    doubleSided: {
      front: '',
      back: ''
    },
    trueFalse: {
      front: '',
      validity: true
    },
    quiz: {
      front: '',
      options: []
    }
  })
  
  /* solution state */
  onSolution = signal(false);
  isCorrect = signal(false);
  /* DOM element of the choosen response */
  choosenInput = signal<HTMLDivElement | undefined>(undefined);
  isDone = signal(false);
  isFinalOutcome = signal(false);
  outcomeCounter = signal<number>(0);
  // 0 <= niceness <= 1
  niceness = computed<number>(() => {
    const niceness = this.outcomeCounter() / this.cardList.length;
    if(niceness < 0 || niceness > 1){
      throw new Error("invalid niceness");
    }
    return niceness;
  });
  isPassed = computed<boolean>(() => {
    return this.niceness() >= 0.5;
  })
  congratsMessage = computed<string>(() => {
    if(this.niceness() < 0.4){
      return "There were to many mistakes. Use Pomo Counter and plan some activities to better study these topics. Have a great study and try again tomorrow!👊";
    }else if(this.niceness() < 0.5){
      return "You almost did it! Use Pomo Counter to review these topics. I'm sure next time you will pass it!💪";
    }else if(this.niceness() < 0.8){
      return "Congratulations! Now you just need to review some topics and next time you will do even better!😎";
    }else if(this.niceness() < 1){
      return "Amazing performance! You almost aced the test! Just do a little review and you are set to a perfect score!💯"
    }else{ // niceness == 1
      return "Too good, you aced the test! You can't do better than this!📚👑"
    }
  });

  /* form init */
  formModel = signal<{ validity: "true" | "false"}>({
    validity: "true"
  });

  respForm = form(this.formModel, (schemaPath) => {
    if(this.currCard().layout === "true-false"){
      required(schemaPath.validity);
    }
  });

  optionFormList = signal<{ id: string, validity: boolean }[]>([]);

  printAnswerText(id: string): string{
    const option = this.currCardInfo().quiz.options.find((opt) => opt.idOption === id);
    if(option === undefined){
      throw new Error("Option undefined for id: " + id);
    }
    return option.answerText;
  }

  updateProgressBar(){
    const fullWidth = (document.querySelector(".progress-bar-container") as HTMLElement).clientWidth;
    const barContent = document.querySelector(".progress") as HTMLElement;
    const contentWidth = (this.currCardCounter() / this.cardList.length) * fullWidth;
    barContent.style.width = `${contentWidth}px`
  }

  onExit(){
    if(this.isFinalOutcome()){
      this.router.navigate([this.username, 'study']);
      return;
    }

    if(confirm("Do you really want to end this study session?")){
      this.router.navigate([this.username, 'study']);
      return;
    }
  }

  onNext(){
    this.onSolution.set(false);
    this.currCardCounter.update((counter) => {
      return counter+1;
    });

    this.updateProgressBar();
    if(this.currCard().layout === "true-false"){
      this.colorTrueFalse(true);
    }

    if(this.currCardCounter() >= this.cardList.length){
      this.isFinalOutcome.set(true);
      return;
    }

    this.currCard.set(this.cardList[this.currCardCounter()]);
    this.isDone.set(false);
    this.getCardInfo();
  }

  setChoosenInput(){
    const inputList = document.querySelectorAll("#true-false-field input") as NodeList;
    let respChecked = '';

    if(!inputList) return;

    inputList.forEach((input) => {
      const radio = input as HTMLInputElement;
      if(radio.checked){
        respChecked = radio.id;
      }
    });

    if(respChecked === "resp2"){
      this.choosenInput.set(document.querySelector("#true-false-field div:nth-child(2)") as HTMLDivElement);
    }else if(respChecked === "resp1"){
      this.choosenInput.set(document.querySelector("#true-false-field div:nth-child(1)") as HTMLDivElement);
    }
  }

  /* change the state of the choosen button for a response to
   * a 'true-false' card. State can be default, correct or wrong. */
  colorTrueFalse(setDefault: boolean){
    if(!this.isDone()){
      this.setChoosenInput();
    }

    const divToColor = this.choosenInput();

    if(!divToColor) return;

    /* clearing class list */
    for(const token of divToColor.classList){
      divToColor.classList.remove(token);
    }

    /* default class */
    if(setDefault){
      divToColor.classList.add("default");
      return;
    }

    /* adding class */
    if(this.isCorrect()){
      divToColor.classList.add("correct");
    }else{
      divToColor.classList.add("wrong");
    }
  }

  /* dynamically adds a message under an option field to reveal its validity */
  revealValidity(optionId: string, isCorrect: boolean){
    const currentLab = document.querySelector(`#quiz-field label[for="${optionId}"]`) as HTMLLabelElement;
    const newPar = document.createElement("p");
    let newParContent;
    if(isCorrect){
      newParContent = document.createTextNode("this option was correct!");
      newPar.style.color = "#28A745";
    }else{
      newParContent = document.createTextNode("this option was wrong!");
      newPar.style.color = "#cb1c16";
    }
    newPar.appendChild(newParContent);
    newPar.style.fontSize = "20px";
    newPar.style.fontWeight = "500";
    currentLab.insertAdjacentElement("afterend", newPar);
  }

  /* change the state of current option based on 'isCorrect' */
  colorOptionField(optionId: string, isCorrect: boolean){
    const labToColor = document.querySelector(`#quiz-field label[for="${optionId}"]`) as HTMLLabelElement;
    if(!labToColor) return;

    let cl = 'correct';
    if(!isCorrect){
      cl = 'wrong';
    }

    labToColor.classList.remove('default');
    labToColor.classList.add(cl);
  }

  computeSolution(): boolean{
    if(this.currCard().layout === "true-false"){
      if(
        (this.respForm.validity().value() === "true" && this.currCardInfo().trueFalse.validity) ||
        (this.respForm.validity().value() === "false" && !this.currCardInfo().trueFalse.validity)
      ){
        return true;
      }else{
        return false;
      }
    }else if(this.currCard().layout === "quiz"){
      const optionList = document.querySelectorAll("#quiz-field input") as NodeList;
      if(!optionList) return false;

      const correctOptionList = this.currCardInfo().quiz.options;
      let invalidResp = false;
      for(const trueOption of correctOptionList){
        optionList.forEach((node) => {
          const current = node as HTMLInputElement;
          if(current.id === trueOption.idOption){

            if(trueOption.validity){
              /* change state as correct */
              this.colorOptionField(current.id, true);
            }else{
              if(current.checked){ /* checked but it was false */
                /* change state as wrong */
                this.colorOptionField(current.id, false);
              }     
            }

            if(current.checked !== trueOption.validity){
              this.revealValidity(current.id, trueOption.validity);
              invalidResp = true;
            }
          }
        })
      }

      if(invalidResp){
        return false;
      }
      return true;
    }else{
      return false; // not used
    }
  }

  outFormModel = signal<"true" | "false">("false");
  outForm = form(this.outFormModel);
  onSubmitOutcome(event: Event){
    event.preventDefault();
    if(this.outForm().value() === "true"){
      this.isCorrect.set(true);
    }else{
      this.isCorrect.set(false);
    }

    if(this.isCorrect()){
      this.outcomeCounter.update((counter) => {
        return counter+1;
      });
      console.log("outcome counter: " + this.outcomeCounter());
    }
    this.isDone.set(true);
  }

  onSubmit(event: Event){
    event.preventDefault();

    if(this.currCard().layout !== "double-sided"){
      this.isCorrect.set(this.computeSolution());
      if(this.isCorrect()){
        this.outcomeCounter.update((counter) => {
          return counter+1;
        });
        console.log("outcome counter: " + this.outcomeCounter());
      }

      if(this.currCard().layout === "true-false"){
        this.colorTrueFalse(false);
      }

      this.isDone.set(true);
    }

    this.onSolution.set(true);
  }

  getCardInfo(){
    const currLayout = this.currCard().layout;
    this.cardService.getDeckCard(this.currCard().cardId, currLayout).subscribe({
      next: (resp) => {
        // curr card init
        switch(currLayout){
          case "double-sided":
            this.currCardInfo.update((card) => {
              card.doubleSided = resp as DoubleSidedCard;
              return card;
            });
            break;
          case "true-false":
            this.currCardInfo.update((card) => {
              card.trueFalse = resp as TrueFalseCard;
              return card;
            });
            break;
          default:
            /* correct options */
            this.currCardInfo.update((card) => {
              card.quiz = resp as QuizCard;
              return card;
            });

            const optList = this.currCardInfo().quiz.options;
            this.optionFormList.update((list) => {
              const newList = [];
              for(const option of optList){
                newList.push({
                  id: option.idOption,
                  validity: false
                });
              }
              return newList;
            })
            break;
        }
      }
    })
  }

  getCardList(){
    this.cardService.getDeckCards(this.deckId).subscribe({
      next: (resp) => {
        this.cardList = resp;
        this.currCardCounter.set(0);
        this.currCard.set(this.cardList[this.currCardCounter()]);
        this.getCardInfo();
      }
    });
  }

  ngOnInit(){
    this.getCardList();
  }
}
