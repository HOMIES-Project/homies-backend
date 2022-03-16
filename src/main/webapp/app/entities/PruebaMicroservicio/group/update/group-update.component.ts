import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IGroup, Group } from '../group.model';
import { GroupService } from '../service/group.service';
import { IUserName } from 'app/entities/PruebaMicroservicio/user-name/user-name.model';
import { UserNameService } from 'app/entities/PruebaMicroservicio/user-name/service/user-name.service';

@Component({
  selector: 'jhi-group-update',
  templateUrl: './group-update.component.html',
})
export class GroupUpdateComponent implements OnInit {
  isSaving = false;

  userNamesSharedCollection: IUserName[] = [];

  editForm = this.fb.group({
    id: [],
    groupKey: [null, [Validators.minLength(10)]],
    groupName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    groupRelationName: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    addGroupDate: [],
    idUserName: [null, [Validators.required]],
    userNames: [],
  });

  constructor(
    protected groupService: GroupService,
    protected userNameService: UserNameService,
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

  trackUserNameById(index: number, item: IUserName): number {
    return item.id!;
  }

  getSelectedUserName(option: IUserName, selectedVals?: IUserName[]): IUserName {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
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
      idUserName: group.idUserName,
      userNames: group.userNames,
    });

    this.userNamesSharedCollection = this.userNameService.addUserNameToCollectionIfMissing(
      this.userNamesSharedCollection,
      ...(group.userNames ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userNameService
      .query()
      .pipe(map((res: HttpResponse<IUserName[]>) => res.body ?? []))
      .pipe(
        map((userNames: IUserName[]) =>
          this.userNameService.addUserNameToCollectionIfMissing(userNames, ...(this.editForm.get('userNames')!.value ?? []))
        )
      )
      .subscribe((userNames: IUserName[]) => (this.userNamesSharedCollection = userNames));
  }

  protected createFromForm(): IGroup {
    return {
      ...new Group(),
      id: this.editForm.get(['id'])!.value,
      groupKey: this.editForm.get(['groupKey'])!.value,
      groupName: this.editForm.get(['groupName'])!.value,
      groupRelationName: this.editForm.get(['groupRelationName'])!.value,
      addGroupDate: this.editForm.get(['addGroupDate'])!.value,
      idUserName: this.editForm.get(['idUserName'])!.value,
      userNames: this.editForm.get(['userNames'])!.value,
    };
  }
}
