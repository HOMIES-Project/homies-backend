import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISpendingList, SpendingList } from '../spending-list.model';
import { SpendingListService } from '../service/spending-list.service';

@Component({
  selector: 'jhi-spending-list-update',
  templateUrl: './spending-list-update.component.html',
})
export class SpendingListUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    total: [null, [Validators.min(0)]],
    nameSpendList: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
  });

  constructor(protected spendingListService: SpendingListService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spendingList }) => {
      this.updateForm(spendingList);
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
    });
  }

  protected createFromForm(): ISpendingList {
    return {
      ...new SpendingList(),
      id: this.editForm.get(['id'])!.value,
      total: this.editForm.get(['total'])!.value,
      nameSpendList: this.editForm.get(['nameSpendList'])!.value,
    };
  }
}
