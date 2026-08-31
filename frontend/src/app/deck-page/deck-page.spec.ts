import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckPage } from './deck-page';

describe('DeckPage', () => {
  let component: DeckPage;
  let fixture: ComponentFixture<DeckPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeckPage],
    }).compileComponents();

    fixture = TestBed.createComponent(DeckPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
