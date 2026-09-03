import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitySettings } from './activity-settings';

describe('ActivitySettings', () => {
  let component: ActivitySettings;
  let fixture: ComponentFixture<ActivitySettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySettings],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivitySettings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
