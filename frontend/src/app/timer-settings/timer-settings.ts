import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { SoundTrack } from '../dto/sound-track';
import { TimerService } from '../service/timer/timer-service';

@Component({
  selector: 'app-timer-settings',
  imports: [],
  templateUrl: './timer-settings.html',
  styleUrl: './timer-settings.css',
})
export class TimerSettings implements OnInit {

  private service = inject(TimerService);

  ringtoneOptions = computed(() => {
    return this.soundList().filter((soundTrack) => soundTrack.type === 'ringtone');
  });
  backgroundOptions = computed(() => {
    return this.soundList().filter((soundTrack) => soundTrack.type === 'background');
  })
  soundList = signal<SoundTrack[]>([]);

  getSoundList(){
    this.service.getAllSounds().subscribe({
      next: (resp) => {
        this.soundList.set(resp);
      },
      error: () => {
        console.log("getAllSounds: error");
      }
    });this.service.getAllSounds().subscribe({
      next: (resp) => {
        this.soundList.set(resp);
      },
      error: () => {
        console.log("getAllSounds: error");
      }
    });
  }

  ngOnInit(): void {
    this.getSoundList();
  }
}
