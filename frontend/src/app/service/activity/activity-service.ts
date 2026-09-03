import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ActivityDTO } from '../../dto/activity-dto';
import { ActivityData } from '../../dto/activity-data';

@Injectable({
  providedIn: 'root',
})
export class ActivityService {
  private baseUrl = '/api/activity';
  private http = inject(HttpClient);

  getUserActivities(username: string){
    return this.http.get<ActivityDTO[]>(`${this.baseUrl}/user/${username}`);
  }

  registerUserActivity(username: string, dataToRegister: ActivityData){
    return this.http.post(`${this.baseUrl}/user/${username}`, dataToRegister);
  }

  updateActivity(activityId: string, updatedData: ActivityData){
    return this.http.put(`${this.baseUrl}/id/${activityId}`, updatedData);
  }

  deleteActivity(activityId: string){
    return this.http.delete(`${this.baseUrl}/id/${activityId}`);
  }

  /* this endpoint registers a pomodoro as related to a given activity but also
   * as a standard pomodoro. 
   * NOTE: When its related activity will be deleted this pomodoro
   * is still going to stay registered on db (only) as a standard pomodoro. */
  sendActivityPomodoro(username: string, activityId: string, timestamp: Date){
    const timestampToSend = timestamp.toISOString();
    return this.http.post(`${this.baseUrl}/user/${username}/pomo`, null, {
      params: {
        activityId: activityId,
        timestamp: timestampToSend
      }
    });
  }
}
