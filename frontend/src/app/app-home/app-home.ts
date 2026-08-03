import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet, RouterLink } from '@angular/router';
import { UserInfo } from '../service/profile/user-info';
import { UserInfoDTO } from '../dto/user-infoDTO';
import { B64toImgPipe } from '../pipes/b64to-img-pipe';

@Component({
  selector: 'app-app-home',
  imports: [RouterOutlet, RouterLink, B64toImgPipe],
  templateUrl: './app-home.html',
  styleUrl: './app-home.css',
})

export class AppHome implements OnInit {
  private router = inject(Router);
  private userService = inject(UserInfo);

  userDTO = signal<UserInfoDTO>({
    email: '',
    profilePhoto: '', 
    photoType: ''
  })

  hasPhoto = computed<boolean>(() => {
    return this.userDTO().profilePhoto !== '';
  });

  getUserInfo(username: string){
    this.userService.getUser(username).subscribe({
      next: (resp) => {
        this.userDTO.set(resp);
        console.log(this.userDTO());
        console.log(this.userDTO().photoType)
      },
      error: (resp) => {
        console.log("get failed: " + resp);
      }
    });
  }

  ngOnInit(){
    const url = this.router.url;
    const username = this.router.url.slice(1, url.indexOf('/', url.indexOf('/')+1));
    this.getUserInfo(username);
  }
}
