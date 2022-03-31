import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IGroup, Group } from '../group.model';
import { GroupService } from '../service/group.service';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { UserDataService } from 'app/entities/Homies/user-data/service/user-data.service';
import { ITaskList } from 'app/entities/task-list/task-list.model';
import { TaskListService } from 'app/entities/task-list/service/task-list.service';

@Component({
  selector: 'jhi-group-update',
  templateUrl: './group-update.component.html',
})
export class GroupUpdateComponent implements OnInit {
  isSaving = false;

  userDataSharedCollection: IUserData[] = [];
  taskListsCollection: ITaskList[] = [];

  editForm = this.fb.group({
    id: [],
    groupKey: [null, [Validators.minLength(10)]],
    groupName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    groupRelationName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    addGroupDate: [],
    userAdmin: [],
    taskList: [],
  });

  constructor(
    protected groupService: GroupService,
    protected userDataService: UserDataService,
    protected taskListService: TaskListService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ group }) => {
      this.updateForm(group);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const group = this.createFromForm();
    if (group.id !== undefined) {
      this.subscribeToSaveResponse(this.groupService.update(group));
    } else {
      this.subscribeToSaveResponse(this.groupService.create(group));
    }
  }

  trackUserDataById(index: number, item: IUserData): number {
    return item.id!;
  }

  trackTaskListById(index: number, item: ITaskList): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGroup>>): void {
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

  protected updateForm(group: IGroup): void {
    this.editForm.patchValue({
      id: group.id,
      groupKey: group.groupKey,
      groupName: group.groupName,
      groupRelationName: group.groupRelationName,
      addGroupDate: group.addGroupDate,
      userAdmin: group.userAdmin,
      taskList: group.taskList,
    });

    this.userDataSharedCollection = this.userDataService.addUserDataToCollectionIfMissing(this.userDataSharedCollection, group.userAdmin);
    this.taskListsCollection = this.taskListService.addTaskListToCollectionIfMissing(this.taskListsCollection, group.taskList);
  }

  protected loadRelationshipsOptions(): void {
    this.userDataService
      .query()
      .pipe(map((res: HttpResponse<IUserData[]>) => res.body ?? []))
      .pipe(
        map((userData: IUserData[]) =>
          this.userDataService.addUserDataToCollectionIfMissing(userData, this.editForm.get('userAdmin')!.value)
        )
      )
      .subscribe((userData: IUserData[]) => (this.userDataSharedCollection = userData));

    this.taskListService
      .query({ 'groupId.specified': 'false' })
      .pipe(map((res: HttpResponse<ITaskList[]>) => res.body ?? []))
      .pipe(
        map((taskLists: ITaskList[]) =>
          this.taskListService.addTaskListToCollectionIfMissing(taskLists, this.editForm.get('taskList')!.value)
        )
      )
      .subscribe((taskLists: ITaskList[]) => (this.taskListsCollection = taskLists));
  }

  protected createFromForm(): IGroup {
    return {
      ...new Group(),
      id: this.editForm.get(['id'])!.value,
      groupKey: this.editForm.get(['groupKey'])!.value,
      groupName: this.editForm.get(['groupName'])!.value,
      groupRelationName: this.editForm.get(['groupRelationName'])!.value,
      addGroupDate: this.editForm.get(['addGroupDate'])!.value,
      userAdmin: this.editForm.get(['userAdmin'])!.value,
      taskList: this.editForm.get(['taskList'])!.value,
    };
  }
}
