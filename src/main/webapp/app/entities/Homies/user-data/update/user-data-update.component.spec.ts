import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserDataService } from '../service/user-data.service';
import { IUserData, UserData } from '../user-data.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';

import { UserDataUpdateComponent } from './user-data-update.component';

describe('UserData Management Update Component', () => {
  let comp: UserDataUpdateComponent;
  let fixture: ComponentFixture<UserDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userDataService: UserDataService;
  let userService: UserService;
  let groupService: GroupService;
  let taskService: TaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserDataUpdateComponent],
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
      .overrideTemplate(UserDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userDataService = TestBed.inject(UserDataService);
    userService = TestBed.inject(UserService);
    groupService = TestBed.inject(GroupService);
    taskService = TestBed.inject(TaskService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userData: IUserData = { id: 456 };
      const user: IUser = { id: 92568 };
      userData.user = user;

      const userCollection: IUser[] = [{ id: 39630 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Group query and add missing value', () => {
      const userData: IUserData = { id: 456 };
      const groups: IGroup[] = [{ id: 87186 }];
      userData.groups = groups;

      const groupCollection: IGroup[] = [{ id: 66036 }];
      jest.spyOn(groupService, 'query').mockReturnValue(of(new HttpResponse({ body: groupCollection })));
      const additionalGroups = [...groups];
      const expectedCollection: IGroup[] = [...additionalGroups, ...groupCollection];
      jest.spyOn(groupService, 'addGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(groupService.query).toHaveBeenCalled();
      expect(groupService.addGroupToCollectionIfMissing).toHaveBeenCalledWith(groupCollection, ...additionalGroups);
      expect(comp.groupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Task query and add missing value', () => {
      const userData: IUserData = { id: 456 };
      const taskAsigneds: ITask[] = [{ id: 32302 }];
      userData.taskAsigneds = taskAsigneds;

      const taskCollection: ITask[] = [{ id: 56974 }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const additionalTasks = [...taskAsigneds];
      const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(taskCollection, ...additionalTasks);
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userData: IUserData = { id: 456 };
      const user: IUser = { id: 53371 };
      userData.user = user;
      const groups: IGroup = { id: 17419 };
      userData.groups = [groups];
      const taskAsigneds: ITask = { id: 75914 };
      userData.taskAsigneds = [taskAsigneds];

      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(userData));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.groupsSharedCollection).toContain(groups);
      expect(comp.tasksSharedCollection).toContain(taskAsigneds);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserData>>();
      const userData = { id: 123 };
      jest.spyOn(userDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userData }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(userDataService.update).toHaveBeenCalledWith(userData);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserData>>();
      const userData = new UserData();
      jest.spyOn(userDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userData }));
      saveSubject.complete();

      // THEN
      expect(userDataService.create).toHaveBeenCalledWith(userData);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserData>>();
      const userData = { id: 123 };
      jest.spyOn(userDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userDataService.update).toHaveBeenCalledWith(userData);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackGroupById', () => {
      it('Should return tracked Group primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGroupById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTaskById', () => {
      it('Should return tracked Task primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTaskById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedGroup', () => {
      it('Should return option if no Group is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedGroup(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Group for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedGroup(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Group is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedGroup(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });

    describe('getSelectedTask', () => {
      it('Should return option if no Task is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedTask(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Task for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedTask(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Task is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedTask(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
