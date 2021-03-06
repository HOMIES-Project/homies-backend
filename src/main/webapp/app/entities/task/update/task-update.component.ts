import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITask, Task } from '../task.model';
import { TaskService } from '../service/task.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITaskList } from 'app/entities/task-list/task-list.model';
import { TaskListService } from 'app/entities/task-list/service/task-list.service';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { UserDataService } from 'app/entities/Homies/user-data/service/user-data.service';

@Component({
  selector: 'jhi-task-update',
  templateUrl: './task-update.component.html',
})
export class TaskUpdateComponent implements OnInit {
  isSaving = false;

  taskListsSharedCollection: ITaskList[] = [];
  userDataSharedCollection: IUserData[] = [];

  editForm = this.fb.group({
    id: [],
    taskName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    dataCreate: [],
    dataEnd: [],
    description: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    cancel: [],
    photo: [],
    photoContentType: [],
    puntuacion: [],
    taskList: [],
    userData: [],
    userCreator: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected taskService: TaskService,
    protected taskListService: TaskListService,
    protected userDataService: UserDataService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ task }) => {
      this.updateForm(task);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('homiesApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const task = this.createFromForm();
    if (task.id !== undefined) {
      this.subscribeToSaveResponse(this.taskService.update(task));
    } else {
      this.subscribeToSaveResponse(this.taskService.create(task));
    }
  }

  trackTaskListById(index: number, item: ITaskList): number {
    return item.id!;
  }

  trackUserDataById(index: number, item: IUserData): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITask>>): void {
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

  protected updateForm(task: ITask): void {
    this.editForm.patchValue({
      id: task.id,
      taskName: task.taskName,
      dataCreate: task.dataCreate,
      dataEnd: task.dataEnd,
      description: task.description,
      cancel: task.cancel,
      photo: task.photo,
      photoContentType: task.photoContentType,
      puntuacion: task.puntuacion,
      taskList: task.taskList,
      userData: task.userData,
      userCreator: task.userCreator,
    });

    this.taskListsSharedCollection = this.taskListService.addTaskListToCollectionIfMissing(this.taskListsSharedCollection, task.taskList);
    this.userDataSharedCollection = this.userDataService.addUserDataToCollectionIfMissing(
      this.userDataSharedCollection,
      task.userData,
      task.userCreator
    );
  }

  protected loadRelationshipsOptions(): void {
    this.taskListService
      .query()
      .pipe(map((res: HttpResponse<ITaskList[]>) => res.body ?? []))
      .pipe(
        map((taskLists: ITaskList[]) =>
          this.taskListService.addTaskListToCollectionIfMissing(taskLists, this.editForm.get('taskList')!.value)
        )
      )
      .subscribe((taskLists: ITaskList[]) => (this.taskListsSharedCollection = taskLists));

    this.userDataService
      .query()
      .pipe(map((res: HttpResponse<IUserData[]>) => res.body ?? []))
      .pipe(
        map((userData: IUserData[]) =>
          this.userDataService.addUserDataToCollectionIfMissing(
            userData,
            this.editForm.get('userData')!.value,
            this.editForm.get('userCreator')!.value
          )
        )
      )
      .subscribe((userData: IUserData[]) => (this.userDataSharedCollection = userData));
  }

  protected createFromForm(): ITask {
    return {
      ...new Task(),
      id: this.editForm.get(['id'])!.value,
      taskName: this.editForm.get(['taskName'])!.value,
      dataCreate: this.editForm.get(['dataCreate'])!.value,
      dataEnd: this.editForm.get(['dataEnd'])!.value,
      description: this.editForm.get(['description'])!.value,
      cancel: this.editForm.get(['cancel'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      puntuacion: this.editForm.get(['puntuacion'])!.value,
      taskList: this.editForm.get(['taskList'])!.value,
      userData: this.editForm.get(['userData'])!.value,
      userCreator: this.editForm.get(['userCreator'])!.value,
    };
  }
}
