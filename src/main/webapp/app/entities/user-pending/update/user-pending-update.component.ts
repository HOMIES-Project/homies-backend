import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUserPending, UserPending } from '../user-pending.model';
import { UserPendingService } from '../service/user-pending.service';
import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { SpendingListService } from 'app/entities/spending-list/service/spending-list.service';
import { ISpending } from 'app/entities/spending/spending.model';
import { SpendingService } from 'app/entities/spending/service/spending.service';

@Component({
  selector: 'jhi-user-pending-update',
  templateUrl: './user-pending-update.component.html',
})
export class UserPendingUpdateComponent implements OnInit {
  isSaving = false;

  spendingListsSharedCollection: ISpendingList[] = [];
  spendingsSharedCollection: ISpending[] = [];

  editForm = this.fb.group({
    id: [],
    pending: [null, [Validators.min(0)]],
    paid: [],
    spendingList: [],
    spendings: [],
  });

  constructor(
    protected userPendingService: UserPendingService,
    protected spendingListService: SpendingListService,
    protected spendingService: SpendingService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userPending }) => {
      this.updateForm(userPending);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userPending = this.createFromForm();
    if (userPending.id !== undefined) {
      this.subscribeToSaveResponse(this.userPendingService.update(userPending));
    } else {
      this.subscribeToSaveResponse(this.userPendingService.create(userPending));
    }
  }

  trackSpendingListById(index: number, item: ISpendingList): number {
    return item.id!;
  }

  trackSpendingById(index: number, item: ISpending): number {
    return item.id!;
  }

  getSelectedSpending(option: ISpending, selectedVals?: ISpending[]): ISpending {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserPending>>): void {
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

  protected updateForm(userPending: IUserPending): void {
    this.editForm.patchValue({
      id: userPending.id,
      pending: userPending.pending,
      paid: userPending.paid,
      spendingList: userPending.spendingList,
      spendings: userPending.spendings,
    });

    this.spendingListsSharedCollection = this.spendingListService.addSpendingListToCollectionIfMissing(
      this.spendingListsSharedCollection,
      userPending.spendingList
    );
    this.spendingsSharedCollection = this.spendingService.addSpendingToCollectionIfMissing(
      this.spendingsSharedCollection,
      ...(userPending.spendings ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.spendingListService
      .query()
      .pipe(map((res: HttpResponse<ISpendingList[]>) => res.body ?? []))
      .pipe(
        map((spendingLists: ISpendingList[]) =>
          this.spendingListService.addSpendingListToCollectionIfMissing(spendingLists, this.editForm.get('spendingList')!.value)
        )
      )
      .subscribe((spendingLists: ISpendingList[]) => (this.spendingListsSharedCollection = spendingLists));

    this.spendingService
      .query()
      .pipe(map((res: HttpResponse<ISpending[]>) => res.body ?? []))
      .pipe(
        map((spendings: ISpending[]) =>
          this.spendingService.addSpendingToCollectionIfMissing(spendings, ...(this.editForm.get('spendings')!.value ?? []))
        )
      )
      .subscribe((spendings: ISpending[]) => (this.spendingsSharedCollection = spendings));
  }

  protected createFromForm(): IUserPending {
    return {
      ...new UserPending(),
      id: this.editForm.get(['id'])!.value,
      pending: this.editForm.get(['pending'])!.value,
      paid: this.editForm.get(['paid'])!.value,
      spendingList: this.editForm.get(['spendingList'])!.value,
      spendings: this.editForm.get(['spendings'])!.value,
    };
  }
}
