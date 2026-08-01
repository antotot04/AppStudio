import { Component, inject, signal } from '@angular/core';
import { form, FormField } from '@angular/forms/signals';
import { RouterLink } from "@angular/router";
import { SignupForm } from '../dto/signup-form';
import { SignupService } from '../service/signup-service';

@Component({
  selector: 'app-app-signup',
  imports: [FormField],
  templateUrl: './app-signup.html',
  styleUrl: './app-signup.css',
})
export class AppSignup {

  formModel = signal<SignupForm>({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  })

  signupForm = form(this.formModel); 
  signupService = inject(SignupService);
  ifMatch = signal<string>("match");

  photo = signal<File | null>(null);

  onPhotoUpload(event: Event){
    const upload = event.target as HTMLInputElement;
    if(upload.files){
      this.photo.set(upload.files[0]);
    }
  }

  onSubmit(event: Event){
    event.preventDefault();

    if(this.signupForm.password().value() !== this.signupForm.confirmPassword().value()){
      this.ifMatch.set("no-match");
      return;
    }

    /* passing everything to a formData object so I can handle image file */

    const data = new FormData();
    data.append("username", this.signupForm.username().value());
    data.append("email",  this.signupForm.email().value());
    data.append("password",  this.signupForm.password().value());
    if(this.photo() !== null){
      data.append("photo", this.photo() as File);
      data.append("photo_type", this.photo()!.type);
    }

    this.signupService.registerUser(data).subscribe({
      next: () => alert("ok"),
      error: () => alert("error")
    })
  }
}
