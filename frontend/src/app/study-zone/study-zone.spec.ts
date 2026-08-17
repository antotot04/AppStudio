import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudyZone } from './study-zone';

describe('StudyZone', () => {
  let component: StudyZone;
  let fixture: ComponentFixture<StudyZone>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudyZone],
    }).compileComponents();

    fixture = TestBed.createComponent(StudyZone);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
