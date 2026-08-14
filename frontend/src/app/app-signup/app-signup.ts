import { Component, inject, signal } from '@angular/core';
import { email, form, FormField, maxLength, required, pattern, minLength } from '@angular/forms/signals';
import { Router, RouterLink } from "@angular/router";
import { SignupForm } from '../dto/signup-form';
import { SignupService } from '../service/access/signup-service';

@Component({
  selector: 'app-app-signup',
  imports: [FormField, RouterLink],
  templateUrl: './app-signup.html',
  styleUrl: './app-signup.css',
})
export class AppSignup {

  private router = inject(Router); 
  private signupService = inject(SignupService);
  ifInvalid = signal<string>("");
  errorMessage = signal<string>('');
  photo: File | null = null;
  chars = signal<number>(30);

  formModel = signal<SignupForm>({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });

  signupForm = form(this.formModel, (schemaPath) => {

    /* username checks */ 
    required(schemaPath.username, {message: "username is required"});
    maxLength(schemaPath.username, 30);

    /* email checks */ 
    required(schemaPath.email, {message: "email is required"});
    email(schemaPath.email, {message: "insert a valid email"});

    /* password checks */ 
    required(schemaPath.password, {message: "password is required"});
    minLength(schemaPath.password, 6, {message: "password is too short"});
    pattern(schemaPath.password, new RegExp(/^(?=.*[\d].*)(?=.*[^\d].*)(?=.*[^\n]$)/), {message: "password invalid"});
  }); 

  lengthLeft(event: Event){
    const input = event.target as HTMLInputElement;

    if(input){
      this.chars.set(30 - input.value.length);
    }
  }

  onPhotoUpload(event: Event){
    const upload = event.target as HTMLInputElement;
    if(upload.files){
      this.photo = upload.files[0];
    }
  }

  onSubmit(event: Event){
    event.preventDefault();

    /* validity checks for additional safety */ 

    if(this.signupForm.username().invalid()){
      this.ifInvalid.set("invalid");
      return;
    }

    if(this.signupForm.email().invalid()){
      this.ifInvalid.set("invalid");
      return;
    }

    if(this.signupForm.password().invalid()){
      this.ifInvalid.set("invalid");
      return;
    }

    if(this.signupForm.password().value() !== this.signupForm.confirmPassword().value()){
      this.ifInvalid.set("invalid");
      return;
    }


    /* preparing the formData object so I can handle image file */

    const data = new FormData();
    data.append("username", this.signupForm.username().value());
    data.append("email",  this.signupForm.email().value());
    data.append("password",  this.signupForm.password().value());
    if(this.photo !== null){
      data.append("photo", this.photo);
      data.append("photoType", this.photo.type);
    }

    this.signupService.registerUser(data).subscribe({
      next: () => {
        this.router.navigate([`/${this.signupForm.username().value()}`]);
      },
      error: (resp) => {
        this.ifInvalid.set("invalid");
        setTimeout(() => this.ifInvalid.set(""), 1000);
        if(resp.status === 409){
          this.errorMessage.set("Username is already in use");
        }else{
          this.errorMessage.set(resp.error.message);
        }
      }
    })
  }
}
