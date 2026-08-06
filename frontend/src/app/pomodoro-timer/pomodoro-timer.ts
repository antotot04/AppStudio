import { Component, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-pomodoro-timer',
  imports: [DatePipe],
  templateUrl: './pomodoro-timer.html',
  styleUrl: './pomodoro-timer.css',
})
export class PomodoroTimer {

  pomodoroTime = 1500; // pomodoro unit: 25 min
  // default pauses (in seconds)
  shortPause = signal(300);
  longPause = signal(900); 

  // default long pause frequency
  longFreqCounter = signal(0);
  longFrequency = signal(4);

  // timer state signals 
  timerState = signal<'' | 'short' | 'long'>('');
  onPause = signal(true);
  currentTime = signal(this.pomodoroTime);
  intervalId = 0;


  prepareLongPause(){
    this.timerState.set('long');
    this.longFreqCounter.set(0); // reset frequency counter 
    this.currentTime.set(this.longPause());
  }

  prepareShortPause(){
    this.timerState.set('short');
    this.longFreqCounter.set(this.longFreqCounter()+1); // increase frequency counter
    this.currentTime.set(this.shortPause());
  }

  preparePomodoro(){
    this.timerState.set('');
    this.currentTime.set(this.pomodoroTime);
  }

  timerTask(){
    this.intervalId = setInterval(() => {
        if(this.currentTime() === 0){
          clearInterval(this.intervalId);

          if(this.longFreqCounter() === this.longFrequency() && this.timerState() === ''){
            this.prepareLongPause();
          }else{
            this.timerState() === '' ? this.prepareShortPause() : this.preparePomodoro();
          }

          this.onPause.set(true);
        }else{
          this.currentTime.set(this.currentTime() - 1);
        }
    }, 1000);
  }
 
  onStart(){
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
}
