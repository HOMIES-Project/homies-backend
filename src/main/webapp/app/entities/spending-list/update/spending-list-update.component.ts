import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISpendingList, SpendingList } from '../spending-list.model';
import { SpendingListService } from '../service/spending-list.service';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';

@Component({
  selector: 'jhi-spending-list-update',
  templateUrl: './spending-list-update.component.html',
})
export class SpendingListUpdateComponent implements OnInit {
  isSaving = false;

  groupsCollection: IGroup[] = [];

  editForm = this.fb.group({
    id: [],
    total: [null, [Validators.min(0)]],
    nameSpendList: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    group: [],
  });

  constructor(
    protected spendingListService: SpendingListService,
    protected groupService: GroupService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spendingList }) => {
      this.updateForm(spendingList);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const spendingList = this.createFromForm();
    if (spendingList.id !== undefined) {
      this.subscribeToSaveResponse(this.spendingListService.update(spendingList));
    } else {
      this.subscribeToSaveResponse(this.spendingListService.create(spendingList));
    }
  }

  trackGroupById(index: number, item: IGroup): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpendingList>>): void {
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

  protected updateForm(spendingList: ISpendingList): void {
    this.editForm.patchValue({
      id: spendingList.id,
      total: spendingList.total,
      nameSpendList: spendingList.nameSpendList,
      group: spendingList.group,
    });

    this.groupsCollection = this.groupService.addGroupToCollectionIfMissing(this.groupsCollection, spendingList.group);
  }

  protected loadRelationshipsOptions(): void {
    this.groupService
      .query({ 'spendingListId.specified': 'false' })
      .pipe(map((res: HttpResponse<IGroup[]>) => res.body ?? []))
      .pipe(map((groups: IGroup[]) => this.groupService.addGroupToCollectionIfMissing(groups, this.editForm.get('group')!.value)))
      .subscribe((groups: IGroup[]) => (this.groupsCollection = groups));
  }

  protected createFromForm(): ISpendingList {
    return {
      ...new SpendingList(),
      id: this.editForm.get(['id'])!.value,
      total: this.editForm.get(['total'])!.value,
      nameSpendList: this.editForm.get(['nameSpendList'])!.value,
      group: this.editForm.get(['group'])!.value,
    };
  }
}
