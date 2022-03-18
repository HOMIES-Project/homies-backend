import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUserData, UserData } from '../user-data.model';
import { UserDataService } from '../service/user-data.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';

@Component({
  selector: 'jhi-user-data-update',
  templateUrl: './user-data-update.component.html',
})
export class UserDataUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  tasksSharedCollection: ITask[] = [];

  editForm = this.fb.group({
    id: [],
    photo: [],
    photoContentType: [],
    phone: [null, [Validators.minLength(6), Validators.maxLength(50)]],
    premium: [null, [Validators.required]],
    birthDate: [],
    addDate: [],
    user: [],
    taskAsigneds: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected userDataService: UserDataService,
    protected userService: UserService,
    protected taskService: TaskService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userData }) => {
      this.updateForm(userData);

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
    const userData = this.createFromForm();
    if (userData.id !== undefined) {
      this.subscribeToSaveResponse(this.userDataService.update(userData));
    } else {
      this.subscribeToSaveResponse(this.userDataService.create(userData));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackTaskById(index: number, item: ITask): number {
    return item.id!;
  }

  getSelectedTask(option: ITask, selectedVals?: ITask[]): ITask {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserData>>): void {
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

  protected updateForm(userData: IUserData): void {
    this.editForm.patchValue({
      id: userData.id,
      photo: userData.photo,
      photoContentType: userData.photoContentType,
      phone: userData.phone,
      premium: userData.premium,
      birthDate: userData.birthDate,
      addDate: userData.addDate,
      user: userData.user,
      taskAsigneds: userData.taskAsigneds,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, userData.user);
    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing(
      this.tasksSharedCollection,
      ...(userData.taskAsigneds ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(
        map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing(tasks, ...(this.editForm.get('taskAsigneds')!.value ?? [])))
      )
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));
  }

  protected createFromForm(): IUserData {
    return {
      ...new UserData(),
      id: this.editForm.get(['id'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      premium: this.editForm.get(['premium'])!.value,
      birthDate: this.editForm.get(['birthDate'])!.value,
      addDate: this.editForm.get(['addDate'])!.value,
      user: this.editForm.get(['user'])!.value,
      taskAsigneds: this.editForm.get(['taskAsigneds'])!.value,
    };
  }
}
