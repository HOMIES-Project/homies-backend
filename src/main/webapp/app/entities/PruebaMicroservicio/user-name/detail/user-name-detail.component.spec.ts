import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserNameDetailComponent } from './user-name-detail.component';

describe('UserName Management Detail Component', () => {
  let comp: UserNameDetailComponent;
  let fixture: ComponentFixture<UserNameDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserNameDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userName: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserNameDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserNameDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userName on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userName).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
