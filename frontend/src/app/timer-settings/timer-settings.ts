import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { SoundTrack } from '../dto/sound-track';
import { TimerService } from '../service/timer/timer-service';
import { PomoSettingsForm } from '../dto/pomo-settings-form';
import { disabled, form, FormField, min } from '@angular/forms/signals';
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
  exitEvent = output<boolean>();

  initSettings: PomoSettingsForm = {
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
  }
  
  formModel = signal<PomoSettingsForm>(this.initSettings);

  settingsForm = form(this.formModel, (schemaPath) => {
    min(schemaPath.timer.shortPause, 1, {message: "short pause time needs to be at least 1 minute"});
    min(schemaPath.timer.longPause, 1, {message: "long pause time needs to be at least 1 minute"});
    min(schemaPath.timer.frequency, 0, {message: "frequency cannot be negative"});
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
    this.service.getAllSounds().subscribe((resp) => {
      this.soundList.set(resp);
    });
  }

  getSettings(){
    this.service.getUserSettings(this.username()).subscribe((resp) => {
      this.initSettings = {
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

      this.formModel.set(this.initSettings);
    })
  }

  updateSettings(settings: UpdateSettingsDTO, background: BackgroundInfo | null){
    const requests = [this.service.updateUserSettings(this.username(), settings)];

    if(background !== null){
      if(this.initSettings.sound.background === ''){
        requests.push(this.service.registerNewBackground(this.username(), background));
      }else{
        requests.push(this.service.updateBackground(this.username(), background));
      }
    }else{
      requests.push(this.service.deleteBackground(this.username()));
    }

    forkJoin(requests).subscribe({
      next: () => {
        this.exitEvent.emit(true); // close settings and inform pomodoro component that new settings has been updated
        console.log("userSettings updated");
      },
      error: () => {
        this.exitEvent.emit(false);
      }
    })
  }

  onSubmit(event: Event){
    event.preventDefault();

    if(this.settingsForm().invalid()){
      return;
    }

    if(this.settingsForm.sound.ringtone().value() === ""){
      return;
    }
  
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
    if(
      this.settingsForm.timer.shortPause().value() !== this.initSettings.timer.shortPause ||
      this.settingsForm.timer.longPause().value() !== this.initSettings.timer.longPause ||
      this.settingsForm.timer.frequency().value() !== this.initSettings.timer.frequency ||
      this.settingsForm.sound.ringtone().value() !== this.initSettings.sound.ringtone ||
      this.settingsForm.sound.ringtone_volume().value() !== this.initSettings.sound.ringtone_volume ||
      this.settingsForm.sound.background().value() !== this.initSettings.sound.background ||
      this.settingsForm.sound.background_volume().value() !== this.initSettings.sound.background_volume
    ){
      if(confirm("Unsaved settings, are you sure you want to exit?")){
        this.exitEvent.emit(false);
      }
    }else{
      this.exitEvent.emit(false);
    }
  }

  ngOnInit(): void {
    this.getSoundList();
    this.getSettings();
  }
}
