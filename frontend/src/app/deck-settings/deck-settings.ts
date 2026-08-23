import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { StudyService } from '../service/study/study-service';
import { DeckDTO } from '../dto/deck-dto';
import { form, required, FormField } from '@angular/forms/signals';
import { Router } from '@angular/router';

@Component({
  selector: 'app-deck-settings',
  imports: [FormField],
  templateUrl: './deck-settings.html',
  styleUrl: './deck-settings.css',
})
export class DeckSettings implements OnInit {
  hasTerminated = output<void>();
  // if this page needs to create a new deck or to update an existing one
  pageFunc = input<'create' | 'edit'>();
  deckId = input<string>();
  initTitle = signal('');
  initLayout = signal('');
  private studyService = inject(StudyService);
  private router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1));
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
    deckLayout: ''
  });

  deckForm = form(this.formModel, (schemaPath) => {
    required(schemaPath.deckTitle, {message: "title is required"});
  });

  createUserDeck(deckData: DeckDTO){
    this.studyService.registerUserDeck(this.username, deckData).subscribe({
      next: () => {
        console.log("deck registered");
        this.hasTerminated.emit();
      },
      error: () => {
        console.log("createUserDeck: error");
      }
    })
  }

  onSubmit(event: Event){
    event.preventDefault();

    this.createUserDeck({
      deckTitle: this.deckForm.deckTitle().value(),
      deckLayout: this.deckForm.deckLayout().value()
    });
  }

  onExit(){
    if(this.deckForm.deckTitle().value() !== this.initTitle() || 
      this.deckForm.deckLayout().value() !== this.initLayout()){
      if(confirm("If you exit your changes won't be saved")){
        this.hasTerminated.emit();
      }
    }else{
      this.hasTerminated.emit();
    }
  }

  getDeckInfo(){
    if(this.deckId() !== undefined){
      this.studyService.getUserDeck(this.username, this.deckId()!).subscribe({
        next: (resp) => {
          this.formModel.set({
            deckTitle: resp.title,
            deckLayout: resp.layout
          });
          this.initTitle.set(resp.title);
          this.initLayout.set(resp.layout);
        },
        error: () => {
          console.log("getDeckInfo: error");
        }
      })
    }
  }

  ngOnInit(){
    this.getDeckInfo();
  }
}
