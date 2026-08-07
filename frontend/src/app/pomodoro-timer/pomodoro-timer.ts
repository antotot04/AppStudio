import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TimerService } from '../service/timer/timer-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pomodoro-timer',
  imports: [DatePipe],
  templateUrl: './pomodoro-timer.html',
  styleUrl: './pomodoro-timer.css',
})
export class PomodoroTimer {

  timerService = inject(TimerService);
  router = inject(Router);
  url = this.router.url;
  username = this.url.slice(this.url.indexOf('/'), this.url.indexOf('/', this.url.indexOf('/') + 1)); 

  readonly pomodoroTime = 3; // pomodoro unit: 25 min
  // default pauses (in seconds)
  shortPause = signal(5);
  longPause = signal(10); 

  // default long pause frequency
  longFreqCounter = signal(0);
  longFrequency = signal(4);

  // timer state signals 
  timerState = signal<'' | 'short' | 'long'>('');
  onPause = signal(true);
  currentTime = signal(this.pomodoroTime);
  intervalId = 0;

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
    const timestamp =  new Date();
    this.timerService.sendTimestamp(this.username, timestamp.toString()).subscribe({
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
          }

          if(this.longFreqCounter() === this.longFrequency() && this.timerState() === ''){
            this.prepareLongPause();
          }else if(this.timerState() === ''){
            this.prepareShortPause();
          }else{
            this.preparePomodoro();
          }

          this.onPause.set(true);
        }else{
          this.currentTime.update((lastValue) => lastValue - 1);
          this.progressWidth();
        }
    }, 1000);
  }
 
  onPlay(){
    this.timerTask();
    this.onPause.set(false);
  }

  onStop(){
    clearInterval(this.intervalId);
    this.onPause.set(true);
  }

  onSkip(){
    this.onStop(); 
    this.preparePomodoro();
  }

  ngOnDestroy(){
    clearInterval(this.intervalId);
  }
}
