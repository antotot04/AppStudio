import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckSettings } from './deck-settings';

describe('DeckSettings', () => {
  let component: DeckSettings;
  let fixture: ComponentFixture<DeckSettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeckSettings],
    }).compileComponents();

    fixture = TestBed.createComponent(DeckSettings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
