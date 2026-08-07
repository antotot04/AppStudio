import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { inject } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TimerService {
  private http = inject(HttpClient);
  private baseUrl = 'api/timer'; 

  sendTimestamp(username: string, timestamp: string){
    return this.http.post(`${this.baseUrl}/${username}/pomodoro`, null, {
      params: {
        timestamp: timestamp
      }
    })
  }
}
