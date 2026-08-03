import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserInfoDTO } from '../../dto/user-infoDTO';

@Injectable({
  providedIn: 'root',
})
export class UserInfo {

  service = inject(HttpClient);
  baseurl = 'api/utenti'; 

  getUser(user: string){
    const url = this.baseurl + `/${user}`;
    return this.service.get<UserInfoDTO>(url);
  }
}
