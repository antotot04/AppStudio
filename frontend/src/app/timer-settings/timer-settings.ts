import { Component, computed, inject, OnInit, output, signal } from '@angular/core';
import { SoundTrack } from '../dto/sound-track';
import { TimerService } from '../service/timer/timer-service';
import { PomoSettingsForm } from '../dto/pomo-settings-form';
import { disabled, form, FormField } from '@angular/forms/signals';

@Component({
  selector: 'app-timer-settings',
  imports: [FormField],
  templateUrl: './timer-settings.html',
  styleUrl: './timer-settings.css',
})
export class TimerSettings implements OnInit {

  private service = inject(TimerService);
  userSettingsEvent = output<PomoSettingsForm>();

  formModel = signal<PomoSettingsForm>({
    timer: {
      short: 5,
      long: 15, 
      frequency: 4
    },
    sound: {
      ringtone: 'cb01f746-9e74-4832-9474-f9309724d32b',
      ringtone_volume: 70,
      background: '',
      background_volume: 30
    }
  });

  settingsForm = form(this.formModel, (schemaPath) => {
    disabled(schemaPath.sound.background_volume, ({valueOf}) => valueOf(schemaPath.sound.background) === '');
  });

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

  onSubmit(event: Event){
    event.preventDefault();
    const userSettings: PomoSettingsForm = {
      timer: {
        short: this.settingsForm.timer.short().value() * 60,
        long: this.settingsForm.timer.long().value() * 60, 
        frequency: this.settingsForm.timer.frequency().value()
      },
      sound: {
        ringtone: this.settingsForm.sound.ringtone().value(),
        ringtone_volume: this.settingsForm.sound.ringtone_volume().value(),
        background: this.settingsForm.sound.background().value(),
        background_volume: this.settingsForm.sound.background_volume().value()
      }
    }
    this.userSettingsEvent.emit(userSettings);
  }

  ngOnInit(): void {
    this.getSoundList();
  }
}
