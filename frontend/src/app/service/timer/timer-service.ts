import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { SoundTrack } from '../../dto/sound-track';
import { UpdateSettingsDTO } from '../../dto/update-settings-dto';
import { BackgroundInfo } from '../../dto/background-info';
import { UserSettings } from '../../dto/user-settings';

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

  getUserSettings(username: string){
    return this.http.get<UserSettings>(`${this.baseUrl}/${username}/settings`);
  }

  registerNewBackground(username: string, background: BackgroundInfo){
    return this.http.post(`${this.baseUrl}/${username}/background`, background)
  }

  updateBackground(username: string, background: BackgroundInfo){
    return this.http.put(`${this.baseUrl}/${username}/background`, background);
  }

  updateUserSettings(username: string, settings: UpdateSettingsDTO){
    return this.http.put(`${this.baseUrl}/${username}/settings`, settings);
  }

  deleteBackground(username: string){
    return this.http.delete(`${this.baseUrl}/${username}/background`);
  }
}
