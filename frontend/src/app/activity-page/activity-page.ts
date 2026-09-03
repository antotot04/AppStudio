import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ActivityService } from '../service/activity/activity-service';
import { form, FormField } from '@angular/forms/signals';
import { ActivityDTO } from '../dto/activity-dto';

@Component({
  selector: 'app-activity-page',
  imports: [FormField],
  templateUrl: './activity-page.html',
  styleUrl: './activity-page.css',
})
export class ActivityPage implements OnInit {
  router = inject(Router);
  actService = inject(ActivityService);
  username = this.router.url.slice(1, this.router.url.indexOf('/', this.router.url.indexOf('/')+1));
  /* activities init */
  activityList: ActivityDTO[] = [];
  activitiesToDisplay = signal<ActivityDTO[]>([]);

  // settings popUp state
  settingsPopUp = signal(false);

  /* search form */
  formModel = signal<{ word: string }>({
    word: '',
  });
  searchForm = form(this.formModel);

  execSearch(){
    this.activitiesToDisplay.update(() =>
      this.activityList.filter((act) => act.title.includes(this.searchForm.word().value()))
    );
  }

  onSubmit(event: Event){
    event.preventDefault();
    this.execSearch();
  }

  onNewActivity(){

  }

  onEdit(activityId: string){

  }

  onDelete(activityId: string){
    if(confirm("Do you really want to delete this activity?")){
      this.actService.deleteActivity(activityId).subscribe({
      next: () => {
        this.loadUserActivities();
      },
      error: () => {
        console.log("deleteFullActivity: error");
      }
    })
    }
  }

  loadUserActivities(){
    this.actService.getUserActivities(this.username).subscribe({
      next: (resp) => {
        this.activityList = resp;
        this.activitiesToDisplay.set(this.activityList);
        this.execSearch();
      },
      error: () => {
        console.log("loadUserActivities: error");
      }
    })
  }

  ngOnInit(){
    this.loadUserActivities();
  }

}
