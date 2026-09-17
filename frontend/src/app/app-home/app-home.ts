import { Component, computed, inject, OnChanges, OnInit, signal } from '@angular/core';
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
  profileOn = signal<boolean>(false);
  currArea = signal('timer');

  userDTO = signal<UserInfoDTO>({
    email: '',
    hasPhoto: false
  })

  getUserInfo(username: string){
    this.userService.getUser(username).subscribe((resp) => {
      this.userDTO.set(resp);
      this.hasPhoto.set(resp.hasPhoto);
    });
  }

  setProfile(){
    this.profileOn() ? this.profileOn.set(false) : this.profileOn.set(true);
  }

  extractFromURL(): string{
    const startIndex = this.url.indexOf(`${this.username}/`)+ (this.username.length + 1);
    const endIndex = this.url.indexOf('/', this.url.indexOf(`${this.username}/`) + (this.username.length + 1));
  
    if(endIndex !== -1){
      return this.url.slice(startIndex, endIndex).toUpperCase();
    }else{
      return this.url.slice(startIndex).toUpperCase();
    }
  }
  
  setCurrArea(event: Event | null){
    let newCurrArea = "TIMER";
    
    if(event === null){
      /* extract from url */
      newCurrArea = this.extractFromURL();
    }else{
      const input = event.target as HTMLAnchorElement;
      newCurrArea = input.textContent;
    }

    console.log(newCurrArea);
    this.currArea.set(newCurrArea);
  }

  /* style the border of the current selected area */
  styleNavArea(event: Event | null){
    this.setCurrArea(event);
    const navEle = document.querySelectorAll("nav ul li a") as NodeList;
    navEle.forEach((aEle) => {
      const currInput = aEle as HTMLAnchorElement;

      if(currInput.textContent.includes(this.currArea())){
        currInput.style.border = "solid 2px black";
      }else{
        currInput.style.border = "none";
      }
    });
  }

  ngOnInit(){
    const url = this.router.url;
    const username = url.slice(1, url.indexOf('/', url.indexOf('/')+1));
    console.log(username);
    this.getUserInfo(username);
    this.styleNavArea(null);
  }
}
