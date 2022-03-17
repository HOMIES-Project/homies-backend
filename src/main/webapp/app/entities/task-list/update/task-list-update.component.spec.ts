import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TaskListService } from '../service/task-list.service';
import { ITaskList, TaskList } from '../task-list.model';

import { TaskListUpdateComponent } from './task-list-update.component';

describe('TaskList Management Update Component', () => {
  let comp: TaskListUpdateComponent;
  let fixture: ComponentFixture<TaskListUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskListService: TaskListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TaskListUpdateComponent],
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
      .overrideTemplate(TaskListUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskListUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskListService = TestBed.inject(TaskListService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const taskList: ITaskList = { id: 456 };

      activatedRoute.data = of({ taskList });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(taskList));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TaskList>>();
      const taskList = { id: 123 };
      jest.spyOn(taskListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskList }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskListService.update).toHaveBeenCalledWith(taskList);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TaskList>>();
      const taskList = new TaskList();
      jest.spyOn(taskListService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskList }));
      saveSubject.complete();

      // THEN
      expect(taskListService.create).toHaveBeenCalledWith(taskList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TaskList>>();
      const taskList = { id: 123 };
      jest.spyOn(taskListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskListService.update).toHaveBeenCalledWith(taskList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
