import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityPage } from './activity-page';

describe('ActivityPage', () => {
  let component: ActivityPage;
  let fixture: ComponentFixture<ActivityPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivityPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
