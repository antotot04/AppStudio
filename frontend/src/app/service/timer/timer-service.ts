import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { inject } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TimerService {
  private http = inject(HttpClient);
  private baseUrl = 'api/timer'; 

  sendTimestamp(){
    return this.http.post(this.baseUrl, null, {
      params: {
        timestamp: (new Date()).toString()
      }
    })
  }
}
