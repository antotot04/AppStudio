import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { SoundTrack } from '../dto/sound-track';
import { TimerService } from '../service/timer/timer-service';
import { PomoSettingsForm } from '../dto/pomo-settings-form';
import { disabled, form, FormField } from '@angular/forms/signals';
import { UpdateSettingsDTO } from '../dto/update-settings-dto';
import { BackgroundInfo } from '../dto/background-info';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-timer-settings',
  imports: [FormField],
  templateUrl: './timer-settings.html',
  styleUrl: './timer-settings.css',
})
export class TimerSettings implements OnInit {

  private service = inject(TimerService);
  username = input<string>('');
  userSettingsEvent = output<boolean>();
  exitEvent = output<boolean>();
  backgroundToCreate = signal<boolean>(true); // default background sound is not given

  formModel = signal<PomoSettingsForm>({
    timer: {
      shortPause: 5, // minutes
      longPause: 15, // minutes 
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
    });
  }

  getSettings(){
    this.service.getUserSettings(this.username()).subscribe({
      next: (resp) => {
        const freshSettings: PomoSettingsForm = {
          timer: {
            shortPause: resp.timer.shortPause / 60,
            longPause: resp.timer.longPause / 60,
            frequency: resp.timer.frequency
          },
          sound: {
            ringtone: resp.suono.ringtone,
            ringtone_volume: resp.suono.ringtone_volume,
            background: resp.suono.background === null ? '' : resp.suono.background,
            background_volume: resp.suono.background_volume
          }
        }

        if(resp.suono.background === null){
          this.backgroundToCreate.set(true);
        }else{
          this.backgroundToCreate.set(false);
        }

        this.formModel.set(freshSettings);
      },
      error: () => {
        console.log("getSettings: error");
      }
    })
  }

  updateSettings(settings: UpdateSettingsDTO, background: BackgroundInfo | null){
    const requests = [this.service.updateUserSettings(this.username(), settings)];

    if(background !== null){
      if(this.backgroundToCreate()){
        requests.push(this.service.registerNewBackground(this.username(), background));
        this.backgroundToCreate.set(false);
      }else{
        requests.push(this.service.updateBackground(this.username(), background));
      }
    }else{
      requests.push(this.service.deleteBackground(this.username()));
      this.backgroundToCreate.set(true);
    }

    forkJoin(requests).subscribe({
      next: () => {
        this.userSettingsEvent.emit(true); // to inform pomodoro component that new settings has been updated
        console.log("userSettings updated");
      },
      error: () => {
        console.log("error");
      }
    })
  }

  onSubmit(event: Event){
    event.preventDefault();
    const userSettings: UpdateSettingsDTO = {
      shortPause: this.settingsForm.timer.shortPause().value() * 60,
      longPause: this.settingsForm.timer.longPause().value() * 60, 
      longFreq: this.settingsForm.timer.frequency().value(),
      ringtone: this.settingsForm.sound.ringtone().value(),
      ringtoneVolume: this.settingsForm.sound.ringtone_volume().value()
    }

    if(this.settingsForm.sound.background().value() !== ''){
      const userBackground: BackgroundInfo = {
        sound: this.settingsForm.sound.background().value(),
        volume: this.settingsForm.sound.background_volume().value()
      }

      this.updateSettings(userSettings, userBackground);
    }else{
      this.updateSettings(userSettings, null);
    }
  }

  onClickExit(){
    this.exitEvent.emit(true);
  }

  ngOnInit(): void {
    this.getSoundList();
    this.getSettings();
  }
}
