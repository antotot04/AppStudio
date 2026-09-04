import { Component, computed, inject, input, output, signal } from '@angular/core';
import { UserInfoDTO } from '../dto/user-infoDTO';
import { Router } from '@angular/router';
import { email, form, FormField, minLength, pattern, required } from "@angular/forms/signals";
import { UserInfo } from '../service/profile/user-info';
import { AuthService } from '../service/access/auth-service';
import { LoginForm } from '../dto/login-form';

@Component({
  selector: 'app-profile-page',
  imports: [FormField],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePage {
  router = inject(Router);
  private userService = inject(UserInfo);
  private authService = inject(AuthService); 
  readonly userInfos = input<UserInfoDTO>();
  username = this.router.url.slice(1, this.router.url.indexOf('/', this.router.url.indexOf('/') + 1));
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

  formModelPassword = signal<{oldPassword: string, newPassword: string, confirmedPassword: string}>({
    oldPassword: '',
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

  updateGeneralInfoError = signal('');
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

    this.userService.updateGeneralInfo(this.username, dataToSend).subscribe({
      next: () => {
        this.updateGeneralInfoError.set('');
        this.generalState.set('updated');
        alert("refresh to see changes");
        setTimeout(() => {this.generalState.set('')}, 1000);
      },
      error: (resp) => {
        if(resp.status === 404){
          this.updateGeneralInfoError.set("User not found");
        }else{
          this.updateGeneralInfoError.set(resp.error.message);
        }
      }
    })
  }

  showPassForm = false;
  onClickChangePass(){
    this.showPassForm = true;
  }

  checkPass = signal(true);
  validationFailed = signal(false);
  validate(event: Event){
    event.preventDefault();

    const credentials: LoginForm = {
      username: this.username,
      password: this.updateFormPassword.oldPassword().value()
    }

    this.authService.verifyLogin(credentials).subscribe({
      next: () => {
        this.checkPass.set(false);
        this.validationFailed.set(this.checkPass()); 
      },
      error: () => {
        this.checkPass.set(true); 
        this.validationFailed.set(this.checkPass());
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

    this.userService.updatePassword(this.username, newPassword).subscribe(() => {
      this.passwordState.set('updated');
      setTimeout(() => this.passwordState.set(''), 1000);
    })
  }

  onLogOut(){
    this.router.navigate(['/login']);
  }

  execDelete(){
    this.userService.deleteAccount(this.username).subscribe(() => {
      this.router.navigate(['/signup']);
    })
  }

  onDeleteAccount(){
    if(confirm("Do you really want to delete your account? (All your data will be permanently deleted)")){
      this.execDelete();
    }
  }

  execDeleteProfilePhoto(){
    this.userService.deleteProfilePhoto(this.username).subscribe({
      next: () => {
        alert("refresh to see changes");
      }
    })
  }

  onDeletePhoto(){
    if(confirm("Do you really want to delete your profile photo?")){
      this.execDeleteProfilePhoto();
    }
  }
}
