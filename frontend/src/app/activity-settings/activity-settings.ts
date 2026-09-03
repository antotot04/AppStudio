import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { ActivityDTO } from '../dto/activity-dto';
import { ActivityData } from '../dto/activity-data';
import { form, min, required, FormField } from '@angular/forms/signals';
import { ActivityService } from '../service/activity/activity-service';
import { Router } from '@angular/router';

interface ActivityFormData extends ActivityData {
  description: string
}

@Component({
  selector: 'app-activity-settings',
  imports: [FormField],
  templateUrl: './activity-settings.html',
  styleUrl: './activity-settings.css',
})
export class ActivitySettings implements OnInit{
  /* basic init & form */
  private service = inject(ActivityService);
  private router = inject(Router);
  username = this.router.url.slice(1, this.router.url.indexOf('/', this.router.url.indexOf('/')+1));
  hasTerminated = output<boolean>();
  pageFunc = input<"edit" | "create">();
  activityData = input<ActivityDTO>();
  formModel = signal<ActivityFormData>({
    title: '',
    description: '',
    pomoCounter: 1
  });
  activityForm = form(this.formModel, (schemaPath) => {
    required(schemaPath.title, { message: "title is required" });
    required(schemaPath.pomoCounter, { message: "duration is required" });
    min(schemaPath.pomoCounter, 0, { message: "you can't select a lower duration than your current pomodoros on this activity" });
  })
  readonly minDurationErrorMessage = "total pomodoro duration cannot be lower than the total of your already completed pomodoros";
  showMinDurationErrorMessage = signal(false);

  onNewActivity = computed<boolean>(() => {
    if(this.pageFunc() === undefined){
      throw new Error("pageFunc is undefined");
    }

    if(this.pageFunc() === "edit"){
      return false;
    }

    return true;
  });

  registerActivity(){
    const data: ActivityData = {
      title: this.activityForm.title().value(),
      description: this.activityForm.description().value() === '' ? undefined : this.activityForm.description().value(),
      pomoCounter: this.activityForm.pomoCounter().value()
    }
    this.service.registerUserActivity(this.username, data).subscribe({
      next: () => {
        this.hasTerminated.emit(true);
      },
      error: () => {
        console.log("registerActivity: error");
      }
    });
  }

  updateActivity(){
    const actData = this.activityData();
    if(actData !== undefined){
      const data: ActivityData = {
        title: this.activityForm.title().value(),
        description: this.activityForm.description().value() === '' ? undefined : this.activityForm.description().value(),
        pomoCounter: this.activityForm.pomoCounter().value()
      }
      this.service.updateActivity(actData.id, data).subscribe({
        next: () => {
          this.hasTerminated.emit(true);
        },
        error: () => {
          console.log("registerActivity: error");
        }
      });
    }
  }

  onSubmit(event: Event){
    event.preventDefault();

    if(this.activityForm().invalid()){
      return;
    }

    const actData = this.activityData();
    if(this.pageFunc() == "edit" && actData !== undefined){
      if(this.activityForm.pomoCounter().value() < actData.currentPomos){
        this.showMinDurationErrorMessage.set(true);
        return;
      }else{
        this.showMinDurationErrorMessage.set(false);
      }
    }

    if(this.pageFunc() === "create"){
      this.registerActivity();
    }else if(this.pageFunc() === "edit"){
      this.updateActivity();
    }
  }

  onExit(){
    if(
    (
      this.pageFunc() === "create" &&
      (
        this.formModel().title !== '' ||
        this.formModel().description !== '' ||
        this.formModel().pomoCounter !== 1
      )
    ) ||
    (
      this.pageFunc() === "edit" &&
      this.activityData() !== undefined &&
      (
        this.formModel().title !== this.activityData()?.title ||
        this.formModel().description !== (this.activityData()?.description === undefined ? '' : this.activityData()?.description) ||
        this.formModel().pomoCounter !== this.activityData()?.pomoCounter
      )
    )){
      if(confirm("There are some unsaved changed. Do you still want to exit this page?")){
        this.hasTerminated.emit(false);
      }
    }else{
      this.hasTerminated.emit(false);
    }
  }

  ngOnInit(){
    const actData = this.activityData();
    if(this.pageFunc() === "edit" && actData !== undefined){
      this.formModel.set({
        title: actData.title,
        description: actData.description === undefined ? '' : actData.description,
        pomoCounter: actData.pomoCounter
      });
    }

    console.log(actData?.currentPomos);
  }
}
