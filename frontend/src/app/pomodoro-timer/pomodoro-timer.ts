import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TimerService } from '../service/timer/timer-service';
import { Router } from '@angular/router';
import { TimerSettings } from '../timer-settings/timer-settings';
import { PomoSettingsForm } from '../dto/pomo-settings-form';

@Component({
  selector: 'app-pomodoro-timer',
  imports: [DatePipe, TimerSettings],
  templateUrl: './pomodoro-timer.html',
  styleUrl: './pomodoro-timer.css',
})

export class PomodoroTimer {

  timerService = inject(TimerService);
  router = inject(Router);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/') + 1)); 
  userSettings = signal<PomoSettingsForm>({
    timer: {
      short: 300, // seconds
      long: 900, // seconds
      frequency: 4
    },
    sound: {
      ringtone: 'cb01f746-9e74-4832-9474-f9309724d32b', // default ringtone id
      ringtone_volume: 70, 
      background: '',
      background_volume: 30
    }
  });

  onPopUpState = signal<'settings' | 'leaderboard' | ''>('');

  readonly pomodoroTime = 10; // pomodoro unit: 25 min
  shortPause = computed(() => {
    return this.userSettings().timer.short;
  });
  longPause = computed(() => {
    return this.userSettings().timer.long;
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

  sendTimestamp(){
    const timestamp = new Date();
    console.log(this.username);
    this.timerService.sendTimestamp(this.username, timestamp).subscribe({
      next: () => {
        console.log("sendTimestamp: ok");
      },
      error: () => {
        console.log("sendTimestamp: error");
      }
    })
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
            this.sendTimestamp();
            if(this.userSettings().sound.background !== ''){
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
      this.userSettings().sound.background !== ''){
      this.playBackground(true);
    }

    this.pause.set(false);
  }

  onStop(){
    clearInterval(this.intervalId);

    if(this.timerState() === '' && 
      this.userSettings().sound.background !== ''){
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
    ringtone.volume = this.userSettings().sound.ringtone_volume * Math.pow(10, -2);
  }

  setBackgroundVolume(event: Event){
    const background = event.target as HTMLAudioElement;
    background.volume = this.userSettings().sound.background_volume * Math.pow(10, -2);
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

  onUserSettings(userSettings: PomoSettingsForm){
    this.userSettings.set(userSettings);
    this.refreshTimer();
  }

  ngOnDestroy(){
    clearInterval(this.intervalId);
  }
}
