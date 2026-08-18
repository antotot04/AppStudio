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

  getUserDeck(username: string, deckId: string){
    return this.http.get<DeckItem>(`${this.url}/${username}/deck`, {
      params: {
        id: deckId
      }
    });
  }

  registerUserDeck(username: string, deckData: DeckDTO){
    return this.http.post(`${this.url}/${username}/deck`, deckData);
  }

  /* cards endpoints */
}
