import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TimerService } from '../service/timer/timer-service';
import { Router } from '@angular/router';
import { TimerSettings } from '../timer-settings/timer-settings';
import { UserSettings } from '../dto/user-settings';
import { TimerLeaderboard } from '../timer-leaderboard/timer-leaderboard';
import { ActivityDTO } from '../dto/activity-dto';
import { ActivityService } from '../service/activity/activity-service';

@Component({
  selector: 'app-pomodoro-timer',
  imports: [DatePipe, TimerSettings, TimerLeaderboard],
  templateUrl: './pomodoro-timer.html',
  styleUrl: './pomodoro-timer.css',
})

export class PomodoroTimer implements OnInit {
  timerService = inject(TimerService);
  activityServivce = inject(ActivityService);
  router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1)); 
  userSettings = signal<UserSettings>({ // defaults
    timer: {
      shortPause: 300, // seconds
      longPause: 900, // seconds
      frequency: 4
    },
    suono: {
      ringtone: 'cb01f746-9e74-4832-9474-f9309724d32b', // ringtone id
      ringtone_volume: 70, 
      background: null,
      background_volume: 30
    }
  });

  activityList = signal<ActivityDTO[]>([]);
  actChecked = signal<string | undefined>(undefined) // id of the current checked activity

  onPopUpState = signal<'settings' | 'leaderboard' | ''>('');

  readonly pomodoroTime = 3; // pomodoro unit: 25 min
  shortPause = computed(() => {
    return this.userSettings().timer.shortPause;
  });
  longPause = computed(() => {
    return this.userSettings().timer.longPause;
  }); 
  longFrequency = computed(() => {
    return this.userSettings().timer.frequency;
  });
  longFreqCounter = signal(0);

  // timer state 
  timerState = signal<'' | 'short' | 'long'>('');
  pause = signal(true);
  currentTime = signal(this.pomodoroTime);
  intervalId = 0;

  playRingtone(command: boolean | 'restart'){
    const audioEle = document.querySelector("audio.ringtone") as HTMLAudioElement;
    if(command === 'restart'){
      audioEle.load();
    }else{
      command ? audioEle.play() : audioEle.pause();
    }
  }

  playBackground(command: boolean){
    const audioEle = document.querySelector("audio.background") as HTMLAudioElement;
    command ? audioEle.play() : audioEle.pause();
  }

  /* progress bar dynamic styling */
  progressColor = computed(() => {
      const progress = document.querySelector(".progress") as HTMLElement;
      if(this.timerState() === ''){
        progress.style.backgroundColor = 'red';
      }else if(this.timerState() === 'short'){
        progress.style.backgroundColor = 'lightblue';
      }else{
        progress.style.backgroundColor = 'blue';
      }
  });
  progressWidth = computed(() => {
    const progressContainer = document.querySelector(".progress-bar-container") as HTMLElement;
    const progressBar = document.querySelector(".progress") as HTMLElement;
    let timeMeasure = this.pomodoroTime;

    if(this.timerState() === 'short'){
      timeMeasure = this.shortPause();
    }else if(this.timerState() === 'long'){
      timeMeasure = this.longPause();
    }

    const computedWidth = (this.currentTime() / timeMeasure) * (progressContainer.clientWidth);
    progressBar.style.width = `${computedWidth}px`;
  });

  sendTimestamp(timestamp: Date){
    this.timerService.sendTimestamp(this.username, timestamp).subscribe();
  }

  sendActTimestamp(timestamp: Date, activityId: string){
    /* NOTE: see comments related to this endpoint in activity-service.ts */
    this.activityServivce.sendActivityPomodoro(this.username, activityId, timestamp).subscribe(() => {
      this.activityList.update((list) => {
        const updatedList = list.map((act) => {
          if(act.id === activityId){
            return {
              ... act,
              currentPomos: act.currentPomos+1
            }
          }else{
            return {
              ... act,
            }
          }
        });
        return updatedList;
      })

      /* check if current activity is completed */
      const selectedAct = this.activityList().find((act) => act.id === activityId);
      if(selectedAct === undefined){
        throw new Error("current selected activity is undefined");
      }

      if(selectedAct.currentPomos === selectedAct.pomoCounter){
        this.actChecked.set(undefined);
      }
    });
  }

  prepareLongPause(){
    this.timerState.set('long');
    this.progressColor();
    this.longFreqCounter.set(0); // reset frequency counter 
    this.currentTime.set(this.longPause());
    this.progressWidth();
  }

  prepareShortPause(){
    this.timerState.set('short');
    this.progressColor();
    this.longFreqCounter.set(this.longFreqCounter()+1); // increase frequency counter
    this.currentTime.set(this.shortPause());
    this.progressWidth();
  }

  preparePomodoro(){
    this.timerState.set('');
    this.progressColor();
    this.currentTime.set(this.pomodoroTime);
    this.progressWidth();
  }

  timerTask(){
    this.intervalId = setInterval(() => {
        if(this.currentTime() === 0){
          clearInterval(this.intervalId);

          if(this.timerState() === ''){
            const timestamp = new Date();
            const currActId = this.actChecked();
            if(currActId !== undefined){
              this.sendActTimestamp(timestamp, currActId);
            }else{
              this.sendTimestamp(timestamp);
            }

            if(this.userSettings().suono.background !== null){
              this.playBackground(false);
            }
            this.playRingtone(true);
          }

          if(this.longFreqCounter() === this.longFrequency() && this.timerState() === ''){
            this.prepareLongPause();
          }else if(this.timerState() === ''){
            this.prepareShortPause();
          }else{
            this.preparePomodoro();
          }

          this.pause.set(true);
        }else{
          this.currentTime.update((lastValue) => lastValue - 1);
          this.progressWidth();
        }
    }, 1000);
  }
 
  onPlay(){
    this.timerTask();

    this.playRingtone(false);
    if(this.timerState() === '' &&
      this.userSettings().suono.background !== null){
      this.playBackground(true);
    }

    this.pause.set(false);
  }

  onStop(){
    clearInterval(this.intervalId);

    if(this.timerState() === '' && 
      this.userSettings().suono.background !== null){
      this.playBackground(false); 
    }

    this.pause.set(true);
  }

  onSkip(){
    this.onStop(); 
    this.preparePomodoro();
    this.playRingtone('restart'); 
  }

  onSettingsClick(){
    this.onPopUpState.set('settings');
  }

  onLeaderboardClick(){
    this.onPopUpState.set('leaderboard');
  }

  setRingtoneVolume(event: Event){
    const ringtone = event.target as HTMLAudioElement;
    ringtone.volume = this.userSettings().suono.ringtone_volume * Math.pow(10, -2);
  }

  setBackgroundVolume(event: Event){
    const background = event.target as HTMLAudioElement;
    background.volume = this.userSettings().suono.background_volume * Math.pow(10, -2);
  }

  toSec(time: string): number{
    const minutes = Number(time.slice(0, 2));
    const seconds = Number(time.slice(3));
    return minutes * 60 + seconds;
  }

  refreshTimer(){
    if(this.timerState() === 'short'){
      this.prepareShortPause();
      this.longFreqCounter.set(0);
    }else if(this.timerState() === 'long'){
      this.prepareLongPause();
    }else if(this.timerState() === ''){
      this.longFreqCounter.set(0);
    }
  }

  getUpdatedSettings(){
    this.timerService.getUserSettings(this.username).subscribe((resp) => {
      this.userSettings.set(resp);
      this.refreshTimer();
    })
  }

  loadCurrUserActivities(){
    this.activityServivce.getUserActivities(this.username).subscribe((resp) => {
      this.activityList.set(resp);
    })
  }

  renderDescription(description: string): string{
    if(description.length > 40){
      return description.slice(0, 40).concat("...");
    }
    return description;
  }

  onMoreDescription(event: Event, description: string, activityId: string){
    const thisBtn = event.target as HTMLButtonElement;
    const target = document.querySelector("#act-description-" + activityId) as HTMLParagraphElement;

    if(thisBtn.textContent === "Show more"){
      target.textContent = "Description:\n" + description;
      thisBtn.textContent = "Hide";
    }else{
      target.textContent = this.renderDescription(description);
      thisBtn.textContent = "Show more";
    }
  }

  onActChange(event: Event){
    const thisCheck = event.target as HTMLInputElement;
    const checkList = document.querySelectorAll("input.act-checkbox") as NodeListOf<HTMLInputElement>;
    if(thisCheck.checked){
      this.actChecked.set(thisCheck.id);
      checkList.forEach((ele) => {
        if(!ele.disabled && ele !== thisCheck){
          ele.checked = false;
        }
      });
    }else{
      this.actChecked.set(undefined);
    }
  }

  activitiesCompleted(): boolean{
    for(const act of this.activityList()){
      if(act.currentPomos !== act.pomoCounter){
        return false;
      }
    }
    return true;
  }

  onUserSettings(settingsUpdated: boolean){
    if(settingsUpdated){
      this.getUpdatedSettings();
    }
  }

  onPopUpExit(condition: boolean){
    if(condition){
      this.onPopUpState.set('');
    }
  }

  ngOnInit(){
    this.getUpdatedSettings();
    this.loadCurrUserActivities();
  }

  ngOnDestroy(){
    clearInterval(this.intervalId);
  }
}
