import { inject, Injectable } from '@angular/core';
import { LoginForm } from '../../dto/login-form';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  url = "/api/utenti/login";
  private http = inject(HttpClient);

  verifyLogin(credentials: LoginForm){
    return this.http.post(
      this.url, 
      {
        username: credentials.username, 
        password: credentials.password
      }, 
      {
        observe: 'response'
      });
  }

}
