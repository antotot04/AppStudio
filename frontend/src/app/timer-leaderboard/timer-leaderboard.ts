import { Component, inject, input, OnInit, output, signal } from '@angular/core';
import { LeaderboardUser } from '../dto/leaderboard-user';
import { TimerService } from '../service/timer/timer-service';

@Component({
  selector: 'app-timer-leaderboard',
  imports: [],
  templateUrl: './timer-leaderboard.html',
  styleUrl: './timer-leaderboard.css',
})
export class TimerLeaderboard implements OnInit {
  private timerService = inject(TimerService);
  exitEvent = output<boolean>();
  username = input<string>();
  hasPhoto = input<boolean>();
  DefaultTimeSpan: "week" | "month" | "year" = "week";
  quantity = signal<number>(10);

  leaderboardUsers = signal<LeaderboardUser[]>([]);
  loggedUser = signal<LeaderboardUser>({
    rank: 0,
    username: '',
    pomodoroCounter: 0
  });

  onClickExit(){
    this.exitEvent.emit(true);
  }

  onSelect(event: Event){
    const input = event.target as HTMLSelectElement;
    const selectedTimeSpan = input.value as "week" | "month" | "year"; // there are no other possible options besides these
    this.getLeaderboardUsers(selectedTimeSpan);
    this.getLoggedUser(selectedTimeSpan)
  }

  getLoggedUser(timeSpan: "week" | "month" | "year"){
    this.timerService.getUserLeaderboardData(this.username()!, timeSpan, this.quantity()).subscribe((resp) => {
      this.loggedUser.set(resp);
    });
  }

  getLeaderboardUsers(timeSpan: "week" | "month" | "year"){
    this.timerService.getLeaderboardData(this.quantity(), timeSpan).subscribe((resp) => {
      this.leaderboardUsers.set(resp);
    });
  }

  ngOnInit(){
    this.getLeaderboardUsers(this.DefaultTimeSpan);
    this.getLoggedUser(this.DefaultTimeSpan);
  }
}
