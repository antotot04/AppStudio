import { Component, signal, inject } from '@angular/core';
import { LoginForm } from '../dto/login-form'
import { form, required, maxLength, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { AuthService } from '../service/auth-service';

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

  authService = inject(AuthService);

  onSubmit(event: Event){
    event.preventDefault(); 

    const credentials: LoginForm = {
      username: this.loginForm.username().value(),
      password: this.loginForm.password().value()
    }

    try{
      this.authService.verifyLogin(credentials).subscribe({
        next: (res) => {
          // TODO: handle 202
        },
        error: (res) => {
          // TODO: handle 401 
        }
      });
    }catch(error){
      console.error("This is the error: " + error); 
    }

  }
}
