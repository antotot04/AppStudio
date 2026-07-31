import { Component, signal } from '@angular/core';
import { LoginForm } from '../dto/login-form'
import { form, required, max, maxLength, FormField } from '@angular/forms/signals';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-app-login',
  imports: [FormField, RouterLink],
  templateUrl: './app-login.html',
  styleUrl: './app-login.css',
})
export class AppLogin {

  loginModel = signal<LoginForm>({
    username: '',
    password: ''
  });

  loginForm = form(this.loginModel, (schemaPath) => {
    required(schemaPath.username, {message: "Username required"});
    required(schemaPath.password, {message: "Password required"});
    maxLength(schemaPath.username, 30);
  });

  onSubmit(event: Event){
    event.preventDefault(); 

    let username: string = this.loginForm.username().value();
    let password: string = this.loginForm.password().value();


    // just for testing (gonna remove it)
    alert("username: " + username + " length: " + username.length + " password : " + password);
  }
}
