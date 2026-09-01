import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardSettings } from './card-settings';

describe('CardSettings', () => {
  let component: CardSettings;
  let fixture: ComponentFixture<CardSettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardSettings],
    }).compileComponents();

    fixture = TestBed.createComponent(CardSettings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
