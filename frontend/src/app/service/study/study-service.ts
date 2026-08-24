import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DeckItem } from '../../dto/deck-item';
import { DeckDTO } from '../../dto/deck-dto';
import { CardItem } from '../../dto/card-item';
import { QuizCard } from '../../dto/quiz-card';
import { TrueFalseCard } from '../../dto/true-false-card';
import { DoubleSidedCard } from '../../dto/double-sided-card';
import { Layouts } from '../../dto/layouts';

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

  /* cards endpoints */
  getDeckCards(deckId: string){
    return this.http.get<CardItem[]>(`${this.url}/deck/${deckId}/cards`);
  }

  getDeckCard(cardId: string){
    return this.http.get<QuizCard | TrueFalseCard | DoubleSidedCard>(`${this.url}/card/${cardId}`);
  }

  reigsterCard(deckId: string, 
    cardLayout: "quiz" | "true-false" | "double-sided", 
    cardData: QuizCard | TrueFalseCard | DoubleSidedCard){
      return this.http.post(`${this.url}/deck/${deckId}/card`, cardData, {
        params: {
          layout: cardLayout
        }
      })
  }

  updateCard(cardId: string,
    cardLayout: "quiz" | "true-false" | "double-sided", 
    cardData: QuizCard | TrueFalseCard | DoubleSidedCard){
      return this.http.put(`${this.url}/card/${cardId}`, cardData, {
        params: {
          layout: cardLayout
        }
      })
  }

  deleteDeckCard(cardId: string){
    return this.http.delete(`${this.url}/card/${cardId}`);
  }

  availableLayouts(deckId: string){
    return this.http.get<Layouts>(`${this.url}/deck/${deckId}/layouts`);
  }
}
