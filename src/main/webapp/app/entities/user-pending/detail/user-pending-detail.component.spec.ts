import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserPendingDetailComponent } from './user-pending-detail.component';

describe('UserPending Management Detail Component', () => {
  let comp: UserPendingDetailComponent;
  let fixture: ComponentFixture<UserPendingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserPendingDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userPending: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserPendingDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserPendingDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userPending on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userPending).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
