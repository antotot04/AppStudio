import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudyDeck } from './study-deck';

describe('StudyDeck', () => {
  let component: StudyDeck;
  let fixture: ComponentFixture<StudyDeck>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudyDeck],
    }).compileComponents();

    fixture = TestBed.createComponent(StudyDeck);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
