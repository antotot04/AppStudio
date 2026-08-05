import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-pomodoro-timer',
  imports: [],
  templateUrl: './pomodoro-timer.html',
  styleUrl: './pomodoro-timer.css',
})
export class PomodoroTimer {
  timerState = signal('');
  onPause = signal(true);
}
