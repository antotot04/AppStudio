import { Component, signal, inject } from '@angular/core';
import { LoginForm } from '../dto/login-form'
import { form, required, maxLength, FormField } from '@angular/forms/signals';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../service/access/auth-service';

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

  private authService = inject(AuthService);
  private router = inject(Router);
  isFailed = signal('');

  loginForm = form(this.loginModel, (schemaPath) => {
    required(schemaPath.username, {message: "Username required"});
    required(schemaPath.password, {message: "Password required"});
    maxLength(schemaPath.username, 30);
  });

  onSubmit(event: Event){
    event.preventDefault(); 

    const credentials: LoginForm = {
      username: this.loginForm.username().value(),
      password: this.loginForm.password().value()
    }

    this.authService.verifyLogin(credentials).subscribe({
      next: () => {
        this.router.navigate([`/${credentials.username}`])
      },
      error: () => {
        this.isFailed.set('failed'); 
      }
    });

  }
}
