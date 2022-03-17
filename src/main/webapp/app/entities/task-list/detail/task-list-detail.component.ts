import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITaskList } from '../task-list.model';

@Component({
  selector: 'jhi-task-list-detail',
  templateUrl: './task-list-detail.component.html',
})
export class TaskListDetailComponent implements OnInit {
  taskList: ITaskList | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskList }) => {
      this.taskList = taskList;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
