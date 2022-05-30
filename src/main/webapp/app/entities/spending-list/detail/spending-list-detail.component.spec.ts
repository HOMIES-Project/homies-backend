import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SpendingListDetailComponent } from './spending-list-detail.component';

describe('SpendingList Management Detail Component', () => {
  let comp: SpendingListDetailComponent;
  let fixture: ComponentFixture<SpendingListDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SpendingListDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ spendingList: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SpendingListDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SpendingListDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load spendingList on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.spendingList).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
