import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ActivityService } from '../service/activity/activity-service';
import { form, FormField } from '@angular/forms/signals';
import { ActivityDTO } from '../dto/activity-dto';
import { ActivitySettings } from "../activity-settings/activity-settings";

@Component({
  selector: 'app-activity-page',
  imports: [FormField, ActivitySettings],
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
  settingsMode = signal<"edit" | "create">("create");
  currActData = signal<ActivityDTO | undefined>(undefined);

  /* search form */
  formModel = signal<{ word: string }>({
    word: '',
  });
  searchForm = form(this.formModel);
  onSearch = signal(false);
  readonly emptySearch = "Activities not found";
  readonly noActivities = "You don't have any activities at the moment. Start by creating one!"
  emptyActivitiesMessage = signal<string>(this.noActivities);

  execSearch(){
    this.activitiesToDisplay.update(() =>
      this.activityList.filter((act) => act.title.includes(this.searchForm.word().value()))
    );
  }

  onSubmit(event: Event){
    event.preventDefault();
    this.onSearch.set(true);
    this.execSearch();
    if(this.activitiesToDisplay().length === 0){
      this.emptyActivitiesMessage.set(this.emptySearch);
    }
  }

  onNewActivity(){
    this.settingsMode.set("create");
    this.currActData.set(undefined);
    this.settingsPopUp.set(true);
  }

  onEdit(activityId: string){
    const actData = this.activityList.find((act) => act.id === activityId);
    if(actData === undefined){
      throw new Error("actData is undefined");
    }
    this.settingsMode.set("edit");
    this.currActData.set(actData);
    this.settingsPopUp.set(true);
  }

  onSettingsTerm(validTerm: boolean){
    this.settingsPopUp.set(false);
    if(validTerm){
      this.loadUserActivities();
    }
  }

  onDelete(activityId: string){
    if(confirm("Do you really want to delete this activity?")){
      this.actService.deleteActivity(activityId).subscribe(() => {
      this.loadUserActivities();
    })
    }
  }

  onDeleteAllCompleted(){
    if(confirm("Do you really want to delete all completed activities?")){
      this.actService.deleteCompletedUserActivities(this.username).subscribe(() => {
        this.loadUserActivities();
      })
    }
  }

  renderDescription(description: string | undefined): string{
    if(!description){
      return '';
    }

    if(description.length > 150){
      return description.slice(0, 150).concat("...");
    }

    return description;
  }

  loadUserActivities(){
    this.actService.getUserActivities(this.username).subscribe((resp) => {
      this.activityList = resp;
      this.activitiesToDisplay.set(this.activityList);
      this.execSearch();
      this.onSearch.set(false);
      if(this.activitiesToDisplay().length === 0){
        this.emptyActivitiesMessage.set(this.noActivities);
      }
    })
  }

  ngOnInit(){
    this.loadUserActivities();
  }

}
