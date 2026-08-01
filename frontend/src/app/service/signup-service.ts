import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SignupService {
  http = inject(HttpClient);
  url = '/api/utenti/register'; 

  registerUser(form: FormData){
    return this.http.post(this.url, form);
  }
}
