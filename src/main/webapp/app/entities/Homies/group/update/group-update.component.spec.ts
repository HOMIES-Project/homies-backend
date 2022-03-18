import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GroupService } from '../service/group.service';
import { IGroup, Group } from '../group.model';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { UserDataService } from 'app/entities/Homies/user-data/service/user-data.service';
import { ITaskList } from 'app/entities/task-list/task-list.model';
import { TaskListService } from 'app/entities/task-list/service/task-list.service';

import { GroupUpdateComponent } from './group-update.component';

describe('Group Management Update Component', () => {
  let comp: GroupUpdateComponent;
  let fixture: ComponentFixture<GroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let groupService: GroupService;
  let userDataService: UserDataService;
  let taskListService: TaskListService;

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
    userDataService = TestBed.inject(UserDataService);
    taskListService = TestBed.inject(TaskListService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserData query and add missing value', () => {
      const group: IGroup = { id: 456 };
      const userData: IUserData[] = [{ id: 10422 }];
      group.userData = userData;
      const userAdmin: IUserData = { id: 31950 };
      group.userAdmin = userAdmin;

      const userDataCollection: IUserData[] = [{ id: 83534 }];
      jest.spyOn(userDataService, 'query').mockReturnValue(of(new HttpResponse({ body: userDataCollection })));
      const additionalUserData = [...userData, userAdmin];
      const expectedCollection: IUserData[] = [...additionalUserData, ...userDataCollection];
      jest.spyOn(userDataService, 'addUserDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(userDataService.query).toHaveBeenCalled();
      expect(userDataService.addUserDataToCollectionIfMissing).toHaveBeenCalledWith(userDataCollection, ...additionalUserData);
      expect(comp.userDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should call taskList query and add missing value', () => {
      const group: IGroup = { id: 456 };
      const taskList: ITaskList = { id: 39478 };
      group.taskList = taskList;

      const taskListCollection: ITaskList[] = [{ id: 21670 }];
      jest.spyOn(taskListService, 'query').mockReturnValue(of(new HttpResponse({ body: taskListCollection })));
      const expectedCollection: ITaskList[] = [taskList, ...taskListCollection];
      jest.spyOn(taskListService, 'addTaskListToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(taskListService.query).toHaveBeenCalled();
      expect(taskListService.addTaskListToCollectionIfMissing).toHaveBeenCalledWith(taskListCollection, taskList);
      expect(comp.taskListsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const group: IGroup = { id: 456 };
      const userData: IUserData = { id: 44372 };
      group.userData = [userData];
      const userAdmin: IUserData = { id: 44832 };
      group.userAdmin = userAdmin;
      const taskList: ITaskList = { id: 36206 };
      group.taskList = taskList;

      activatedRoute.data = of({ group });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(group));
      expect(comp.userDataSharedCollection).toContain(userData);
      expect(comp.userDataSharedCollection).toContain(userAdmin);
      expect(comp.taskListsCollection).toContain(taskList);
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
    describe('trackUserDataById', () => {
      it('Should return tracked UserData primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserDataById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTaskListById', () => {
      it('Should return tracked TaskList primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTaskListById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedUserData', () => {
      it('Should return option if no UserData is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedUserData(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected UserData for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedUserData(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this UserData is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedUserData(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
