import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet, RouterLink } from '@angular/router';
import { UserInfo } from '../service/profile/user-info';
import { UserInfoDTO } from '../dto/user-infoDTO';
import { ProfilePage } from '../profile-page/profile-page';

@Component({
  selector: 'app-app-home',
  imports: [RouterOutlet, RouterLink, ProfilePage],
  templateUrl: './app-home.html',
  styleUrl: './app-home.css',
})

export class AppHome implements OnInit {
  private router = inject(Router);
  private userService = inject(UserInfo);
  url = this.router.url;
  username = this.url.slice(1, this.url.indexOf('/', this.url.indexOf('/')+1));
  profilePhotoUrl = `/api/utenti/${this.username}/profilePhoto`;
  hasPhoto = signal<boolean>(false);

  userDTO = signal<UserInfoDTO>({
    email: '',
    hasPhoto: false
  })

  getUserInfo(username: string){
    this.userService.getUser(username).subscribe({
      next: (resp) => {
        this.userDTO.set(resp);
        this.hasPhoto.set(resp.hasPhoto);
      },
      error: (resp) => {
        console.log("get failed: " + resp);
      }
    });
  }

  profileOn = signal<boolean>(false);

  setProfile(){
    this.profileOn() ? this.profileOn.set(false) : this.profileOn.set(true);
  }

  ngOnInit(){
    const url = this.router.url;
    const username = url.slice(1, url.indexOf('/', url.indexOf('/')+1));
    console.log(username);
    this.getUserInfo(username);
  }
}
