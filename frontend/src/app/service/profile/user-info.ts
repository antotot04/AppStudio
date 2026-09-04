import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserInfoDTO } from '../../dto/user-infoDTO';

@Injectable({
  providedIn: 'root',
})
export class UserInfo {

  private service = inject(HttpClient);
  private baseurl = 'api/utenti'; 

  getUser(user: string){
    const url = this.baseurl + `/${user}`;
    return this.service.get<UserInfoDTO>(url);
  }

  updateGeneralInfo(user: string, newData: FormData){
    const url = this.baseurl + `/${user}`;
    return this.service.put(url, newData);
  }

  updatePassword(user: string, newPassword: string){
    const url = this.baseurl + `/${user}/password`;
    return this.service.put(url, null, {
      params: {
        passwToUpdate: newPassword
      }
    });
  }

  deleteAccount(user: string){
    const url = this.baseurl + `/${user}`;
    return this.service.delete(url); 
  }

  deleteProfilePhoto(user: string){
    const url = this.baseurl + `/${user}` + '/profilePhoto';
    return this.service.delete(url);
  }
}
