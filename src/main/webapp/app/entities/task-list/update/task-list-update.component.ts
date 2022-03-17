import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITaskList, TaskList } from '../task-list.model';
import { TaskListService } from '../service/task-list.service';

@Component({
  selector: 'jhi-task-list-update',
  templateUrl: './task-list-update.component.html',
})
export class TaskListUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nameList: [null, [Validators.minLength(3), Validators.maxLength(20)]],
  });

  constructor(protected taskListService: TaskListService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskList }) => {
      this.updateForm(taskList);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taskList = this.createFromForm();
    if (taskList.id !== undefined) {
      this.subscribeToSaveResponse(this.taskListService.update(taskList));
    } else {
      this.subscribeToSaveResponse(this.taskListService.create(taskList));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaskList>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(taskList: ITaskList): void {
    this.editForm.patchValue({
      id: taskList.id,
      nameList: taskList.nameList,
    });
  }

  protected createFromForm(): ITaskList {
    return {
      ...new TaskList(),
      id: this.editForm.get(['id'])!.value,
      nameList: this.editForm.get(['nameList'])!.value,
    };
  }
}
