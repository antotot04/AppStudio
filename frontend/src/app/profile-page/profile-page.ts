import { Component, computed, inject, input, output, signal } from '@angular/core';
import { UserInfoDTO } from '../dto/user-infoDTO';
import { Router } from '@angular/router';
import { email, form, FormField, minLength, pattern, required, schema } from "@angular/forms/signals";
import { UpdateForm } from '../dto/update-form';
import { UserInfo } from '../service/profile/user-info';

@Component({
  selector: 'app-profile-page',
  imports: [FormField],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePage {
  router = inject(Router);
  private userService = inject(UserInfo);
  readonly userInfos = input<UserInfoDTO>();
  close = output<void>(); 
  onExit(){
    this.close.emit();
  }

  generalState = signal<string>('');
  generalButtonText = computed<string>(() => {
    if(this.generalState() === 'updated'){
      return 'info updated';
    }else{
      return 'update';
    }
  })

  passwordState = signal<string>('');
  passwordButtonText = computed<string>(() => {
    if(this.passwordState() === 'updated'){
      return 'password updated';
    }else{
      return 'update password';
    }
  })

  formModelGeneral = signal<{email: string}>({
    email: ''
  });

  formModelPassword = signal<{newPassword: string, confirmedPassword: string}>({
    newPassword: '',
    confirmedPassword: ''
  });

  updateFormGeneral = form(this.formModelGeneral, (schemaPath) => {
    email(schemaPath.email, {message: "insert a valid email"});
  });

  updateFormPassword = form(this.formModelPassword, (schemaPath) => {
    required(schemaPath.newPassword, {message: "a new password is required"});
    minLength(schemaPath.newPassword, 6, {message: "password is too short"});
    pattern(schemaPath.newPassword, new RegExp(/^(?=.*[\d].*)(?=.*[^\d].*)(?=.*[^\n]$)/), {message: "password invalid"});
  });


  newImage: File | null = null;

  onUpdatePhoto(event: Event){
    const input = event.target as HTMLInputElement;
    if(input.files){
      this.newImage = input.files[0];
    }
  }

  onSubmitGeneral(event: Event){
    event.preventDefault();

    if(this.updateFormGeneral.email().invalid()){
      this.generalState.set('not-valid');
      return;
    }

    const dataToSend = new FormData();
    dataToSend.append('email', this.updateFormGeneral.email().value());
    if(this.newImage !== null){
      dataToSend.append('photo', this.newImage);
      dataToSend.append('photoType', this.newImage.type);
    }

    this.userService.updateGeneralInfo(this.router.url.slice(1), dataToSend).subscribe({
      next: () => {
        this.generalState.set('updated');
      },
      error: () => {
        console.log("backend error");
      }
    })
  }

  onSubmitPassword(event: Event){
    event.preventDefault();

    if(this.updateFormPassword.newPassword().invalid()){
      this.passwordState.set('not-valid');
      return;
    }

    if(this.updateFormPassword.newPassword().value() !== this.updateFormPassword.confirmedPassword().value()){
      this.passwordState.set('not-valid');
      return;
    }

    const newPassword = this.updateFormPassword.newPassword().value();
    console.log(newPassword);

    this.userService.updatePassword(this.router.url.slice(1), newPassword).subscribe({
      next: () => {
        this.passwordState.set('updated');
      },
      error: () => {
        console.log("backend error");
      }
    })
  }
}
