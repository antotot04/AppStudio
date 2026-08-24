import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DeckItem } from '../../dto/deck-item';
import { DeckDTO } from '../../dto/deck-dto';

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
    return this.http.delete(`${this.url}/deck/${deckId}`)
  }

  /* cards endpoints */
}
