import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISpending, Spending } from '../spending.model';
import { SpendingService } from '../service/spending.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-spending-update',
  templateUrl: './spending-update.component.html',
})
export class SpendingUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    payer: [null, [Validators.required]],
    nameCost: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    cost: [null, [Validators.required, Validators.min(0)]],
    photo: [],
    photoContentType: [],
    descripcion: [null, [Validators.maxLength(100)]],
    paid: [null, [Validators.min(0)]],
    pending: [null, [Validators.min(0)]],
    finished: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected spendingService: SpendingService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spending }) => {
      this.updateForm(spending);
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
    const spending = this.createFromForm();
    if (spending.id !== undefined) {
      this.subscribeToSaveResponse(this.spendingService.update(spending));
    } else {
      this.subscribeToSaveResponse(this.spendingService.create(spending));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpending>>): void {
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

  protected updateForm(spending: ISpending): void {
    this.editForm.patchValue({
      id: spending.id,
      payer: spending.payer,
      nameCost: spending.nameCost,
      cost: spending.cost,
      photo: spending.photo,
      photoContentType: spending.photoContentType,
      descripcion: spending.descripcion,
      paid: spending.paid,
      pending: spending.pending,
      finished: spending.finished,
    });
  }

  protected createFromForm(): ISpending {
    return {
      ...new Spending(),
      id: this.editForm.get(['id'])!.value,
      payer: this.editForm.get(['payer'])!.value,
      nameCost: this.editForm.get(['nameCost'])!.value,
      cost: this.editForm.get(['cost'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      descripcion: this.editForm.get(['descripcion'])!.value,
      paid: this.editForm.get(['paid'])!.value,
      pending: this.editForm.get(['pending'])!.value,
      finished: this.editForm.get(['finished'])!.value,
    };
  }
}
