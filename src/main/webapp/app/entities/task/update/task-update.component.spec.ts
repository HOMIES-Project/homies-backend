import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TaskService } from '../service/task.service';
import { ITask, Task } from '../task.model';
import { ITaskList } from 'app/entities/task-list/task-list.model';
import { TaskListService } from 'app/entities/task-list/service/task-list.service';

import { TaskUpdateComponent } from './task-update.component';

describe('Task Management Update Component', () => {
  let comp: TaskUpdateComponent;
  let fixture: ComponentFixture<TaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskService: TaskService;
  let taskListService: TaskListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TaskUpdateComponent],
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
      .overrideTemplate(TaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskService = TestBed.inject(TaskService);
    taskListService = TestBed.inject(TaskListService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TaskList query and add missing value', () => {
      const task: ITask = { id: 456 };
      const taskList: ITaskList = { id: 3403 };
      task.taskList = taskList;

      const taskListCollection: ITaskList[] = [{ id: 3609 }];
      jest.spyOn(taskListService, 'query').mockReturnValue(of(new HttpResponse({ body: taskListCollection })));
      const additionalTaskLists = [taskList];
      const expectedCollection: ITaskList[] = [...additionalTaskLists, ...taskListCollection];
      jest.spyOn(taskListService, 'addTaskListToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(taskListService.query).toHaveBeenCalled();
      expect(taskListService.addTaskListToCollectionIfMissing).toHaveBeenCalledWith(taskListCollection, ...additionalTaskLists);
      expect(comp.taskListsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const task: ITask = { id: 456 };
      const taskList: ITaskList = { id: 51798 };
      task.taskList = taskList;

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(task));
      expect(comp.taskListsSharedCollection).toContain(taskList);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = { id: 123 };
      jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: task }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskService.update).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = new Task();
      jest.spyOn(taskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: task }));
      saveSubject.complete();

      // THEN
      expect(taskService.create).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = { id: 123 };
      jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskService.update).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTaskListById', () => {
      it('Should return tracked TaskList primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTaskListById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
