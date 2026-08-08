import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { SoundTrack } from '../../dto/sound-track';

@Injectable({
  providedIn: 'root',
})
export class TimerService {
  private http = inject(HttpClient);
  private baseUrl = 'api/timer'; 

  sendTimestamp(username: string, timestamp: Date){
    const timestampToSend = timestamp.toISOString();
    return this.http.post(`${this.baseUrl}/${username}/pomodoro`, null, {
      params: {
        timestamp: timestampToSend
      }
    });
  }

  getAllSounds(){
    return this.http.get<SoundTrack[]>(`${this.baseUrl}/sounds`);
  }
}
