import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IProducts, Products } from '../products.model';
import { ProductsService } from '../service/products.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-products-update',
  templateUrl: './products-update.component.html',
})
export class ProductsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    price: [],
    photo: [],
    photoContentType: [],
    units: [null, [Validators.required, Validators.min(1)]],
    typeUnit: [],
    note: [null, [Validators.maxLength(256)]],
    dataCreated: [],
    shoppingDate: [],
    purchased: [],
    userCreated: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected productsService: ProductsService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ products }) => {
      this.updateForm(products);
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
    const products = this.createFromForm();
    if (products.id !== undefined) {
      this.subscribeToSaveResponse(this.productsService.update(products));
    } else {
      this.subscribeToSaveResponse(this.productsService.create(products));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducts>>): void {
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

  protected updateForm(products: IProducts): void {
    this.editForm.patchValue({
      id: products.id,
      name: products.name,
      price: products.price,
      photo: products.photo,
      photoContentType: products.photoContentType,
      units: products.units,
      typeUnit: products.typeUnit,
      note: products.note,
      dataCreated: products.dataCreated,
      shoppingDate: products.shoppingDate,
      purchased: products.purchased,
      userCreated: products.userCreated,
    });
  }

  protected createFromForm(): IProducts {
    return {
      ...new Products(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      price: this.editForm.get(['price'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      units: this.editForm.get(['units'])!.value,
      typeUnit: this.editForm.get(['typeUnit'])!.value,
      note: this.editForm.get(['note'])!.value,
      dataCreated: this.editForm.get(['dataCreated'])!.value,
      shoppingDate: this.editForm.get(['shoppingDate'])!.value,
      purchased: this.editForm.get(['purchased'])!.value,
      userCreated: this.editForm.get(['userCreated'])!.value,
    };
  }
}
