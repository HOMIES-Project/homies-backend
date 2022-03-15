import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserNameService } from '../service/user-name.service';
import { IUserName, UserName } from '../user-name.model';
import { IUserData } from 'app/entities/PruebaMicroservicio/user-data/user-data.model';
import { UserDataService } from 'app/entities/PruebaMicroservicio/user-data/service/user-data.service';

import { UserNameUpdateComponent } from './user-name-update.component';

describe('UserName Management Update Component', () => {
  let comp: UserNameUpdateComponent;
  let fixture: ComponentFixture<UserNameUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userNameService: UserNameService;
  let userDataService: UserDataService;

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
    userDataService = TestBed.inject(UserDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call userData query and add missing value', () => {
      const userName: IUserName = { id: 456 };
      const userData: IUserData = { id: 4592 };
      userName.userData = userData;

      const userDataCollection: IUserData[] = [{ id: 39055 }];
      jest.spyOn(userDataService, 'query').mockReturnValue(of(new HttpResponse({ body: userDataCollection })));
      const expectedCollection: IUserData[] = [userData, ...userDataCollection];
      jest.spyOn(userDataService, 'addUserDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userName });
      comp.ngOnInit();

      expect(userDataService.query).toHaveBeenCalled();
      expect(userDataService.addUserDataToCollectionIfMissing).toHaveBeenCalledWith(userDataCollection, userData);
      expect(comp.userDataCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userName: IUserName = { id: 456 };
      const userData: IUserData = { id: 44048 };
      userName.userData = userData;

      activatedRoute.data = of({ userName });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(userName));
      expect(comp.userDataCollection).toContain(userData);
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

  describe('Tracking relationships identifiers', () => {
    describe('trackUserDataById', () => {
      it('Should return tracked UserData primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserDataById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
