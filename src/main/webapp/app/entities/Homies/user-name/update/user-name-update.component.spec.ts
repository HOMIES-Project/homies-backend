import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserNameService } from '../service/user-name.service';
import { IUserName, UserName } from '../user-name.model';

import { UserNameUpdateComponent } from './user-name-update.component';

describe('UserName Management Update Component', () => {
  let comp: UserNameUpdateComponent;
  let fixture: ComponentFixture<UserNameUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userNameService: UserNameService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserNameUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UserNameUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserNameUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userNameService = TestBed.inject(UserNameService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const userName: IUserName = { id: 456 };

      activatedRoute.data = of({ userName });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(userName));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserName>>();
      const userName = { id: 123 };
      jest.spyOn(userNameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userName });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userName }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(userNameService.update).toHaveBeenCalledWith(userName);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserName>>();
      const userName = new UserName();
      jest.spyOn(userNameService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userName });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userName }));
      saveSubject.complete();

      // THEN
      expect(userNameService.create).toHaveBeenCalledWith(userName);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserName>>();
      const userName = { id: 123 };
      jest.spyOn(userNameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userName });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userNameService.update).toHaveBeenCalledWith(userName);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
