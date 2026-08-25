import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DeckItem } from '../../dto/deck-item';
import { DeckDTO } from '../../dto/deck-dto';
import { CardItem } from '../../dto/card-item';
import { QuizCard } from '../../dto/quiz-card';
import { TrueFalseCard } from '../../dto/true-false-card';
import { DoubleSidedCard } from '../../dto/double-sided-card';

@Injectable({
  providedIn: 'root',
})
export class StudyService {
  private url = 'api/study';
  private http = inject(HttpClient);

  /* deck endpoints */
  getAllDecks(username: string){
    return this.http.get<DeckItem[]>(`${this.url}/${username}/decks`);
  }

  getUserDeck(deckId: string){
    return this.http.get<DeckItem>(`${this.url}/deck/${deckId}`);
  }

  registerUserDeck(username: string, deckData: DeckDTO){
    return this.http.post(`${this.url}/${username}/deck`, deckData);
  }

  updateUserDeck(deckId: string, deckData: DeckDTO){
    return this.http.put(`${this.url}/deck/${deckId}`, deckData);
  }

  deleteUserDeck(deckId: string){
    return this.http.delete(`${this.url}/deck/${deckId}`);
  }

  getDeckCards(deckId: string){
    return this.http.get<CardItem[]>(`${this.url}/deck/${deckId}/cards`);
  }

  getDeckCard(cardId: string, layout: "quiz" | "true-false" | "double-sided"){
    return this.http.get<QuizCard | TrueFalseCard | DoubleSidedCard>(`${this.url}/card/${cardId}/${layout}`);
  }

  reigsterCard(deckId: string, 
    cardLayout: "quiz" | "true-false" | "double-sided", 
    cardData: QuizCard | TrueFalseCard | DoubleSidedCard){
    return this.http.post(`${this.url}/deck/${deckId}/card/${cardLayout}`, cardData);
  }

  updateCard(cardId: string,
    cardLayout: "quiz" | "true-false" | "double-sided", 
    cardData: QuizCard | TrueFalseCard | DoubleSidedCard){
    return this.http.put(`${this.url}/card/${cardId}/${cardLayout}`, cardData);
  }

  deleteDeckCard(cardId: string, layout: "quiz" | "true-false" | "double-sided"){
    return this.http.delete(`${this.url}/card/${layout}/${cardId}`);
  }

  deleteQuizOption(cardId: string, optionText: string){
    return this.http.delete(`${this.url}/card/quiz/option`, {
      params: {
        optionId: cardId,
        text: optionText
      }
    })
  }
}
