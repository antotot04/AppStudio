import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimerLeaderboard } from './timer-leaderboard';

describe('TimerLeaderboard', () => {
  let component: TimerLeaderboard;
  let fixture: ComponentFixture<TimerLeaderboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimerLeaderboard],
    }).compileComponents();

    fixture = TestBed.createComponent(TimerLeaderboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
