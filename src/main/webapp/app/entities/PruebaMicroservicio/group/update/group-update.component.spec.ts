import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GroupService } from '../service/group.service';
import { IGroup, Group } from '../group.model';
import { IUserName } from 'app/entities/PruebaMicroservicio/user-name/user-name.model';
import { UserNameService } from 'app/entities/PruebaMicroservicio/user-name/service/user-name.service';

import { GroupUpdateComponent } from './group-update.component';

describe('Group Management Update Component', () => {
  let comp: GroupUpdateComponent;
  let fixture: ComponentFixture<GroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let groupService: GroupService;
  let userNameService: UserNameService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GroupUpdateComponent],
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
      .overrideTemplate(GroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    groupService = TestBed.inject(GroupService);
    userNameService = TestBed.inject(UserNameService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserName query and add missing value', () => {
      const group: IGroup = { id: 456 };
      const userNames: IUserName[] = [{ id: 61490 }];
      group.userNames = userNames;

      const userNameCollection: IUserName[] = [{ id: 82548 }];
      jest.spyOn(userNameService, 'query').mockReturnValue(of(new HttpResponse({ body: userNameCollection })));
      const additionalUserNames = [...userNames];
      const expectedCollection: IUserName[] = [...additionalUserNames, ...userNameCollection];
      jest.spyOn(userNameService, 'addUserNameToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(userNameService.query).toHaveBeenCalled();
      expect(userNameService.addUserNameToCollectionIfMissing).toHaveBeenCalledWith(userNameCollection, ...additionalUserNames);
      expect(comp.userNamesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const group: IGroup = { id: 456 };
      const userNames: IUserName = { id: 48615 };
      group.userNames = [userNames];

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(group));
      expect(comp.userNamesSharedCollection).toContain(userNames);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Group>>();
      const group = { id: 123 };
      jest.spyOn(groupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ group });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: group }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(groupService.update).toHaveBeenCalledWith(group);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Group>>();
      const group = new Group();
      jest.spyOn(groupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ group });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: group }));
      saveSubject.complete();

      // THEN
      expect(groupService.create).toHaveBeenCalledWith(group);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Group>>();
      const group = { id: 123 };
      jest.spyOn(groupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ group });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(groupService.update).toHaveBeenCalledWith(group);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserNameById', () => {
      it('Should return tracked UserName primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserNameById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedUserName', () => {
      it('Should return option if no UserName is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedUserName(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected UserName for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedUserName(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this UserName is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedUserName(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
