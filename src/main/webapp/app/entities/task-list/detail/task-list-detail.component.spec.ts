import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TaskListDetailComponent } from './task-list-detail.component';

describe('TaskList Management Detail Component', () => {
  let comp: TaskListDetailComponent;
  let fixture: ComponentFixture<TaskListDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TaskListDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ taskList: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TaskListDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TaskListDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load taskList on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.taskList).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
