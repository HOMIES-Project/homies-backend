import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IShoppingList, ShoppingList } from '../shopping-list.model';
import { ShoppingListService } from '../service/shopping-list.service';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';

@Component({
  selector: 'jhi-shopping-list-update',
  templateUrl: './shopping-list-update.component.html',
})
export class ShoppingListUpdateComponent implements OnInit {
  isSaving = false;

  groupsCollection: IGroup[] = [];

  editForm = this.fb.group({
    id: [],
    total: [],
    nameShopList: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    group: [],
  });

  constructor(
    protected shoppingListService: ShoppingListService,
    protected groupService: GroupService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shoppingList }) => {
      this.updateForm(shoppingList);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shoppingList = this.createFromForm();
    if (shoppingList.id !== undefined) {
      this.subscribeToSaveResponse(this.shoppingListService.update(shoppingList));
    } else {
      this.subscribeToSaveResponse(this.shoppingListService.create(shoppingList));
    }
  }

  trackGroupById(index: number, item: IGroup): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShoppingList>>): void {
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

  protected updateForm(shoppingList: IShoppingList): void {
    this.editForm.patchValue({
      id: shoppingList.id,
      total: shoppingList.total,
      nameShopList: shoppingList.nameShopList,
      group: shoppingList.group,
    });

    this.groupsCollection = this.groupService.addGroupToCollectionIfMissing(this.groupsCollection, shoppingList.group);
  }

  protected loadRelationshipsOptions(): void {
    this.groupService
      .query({ 'shoppingListId.specified': 'false' })
      .pipe(map((res: HttpResponse<IGroup[]>) => res.body ?? []))
      .pipe(map((groups: IGroup[]) => this.groupService.addGroupToCollectionIfMissing(groups, this.editForm.get('group')!.value)))
      .subscribe((groups: IGroup[]) => (this.groupsCollection = groups));
  }

  protected createFromForm(): IShoppingList {
    return {
      ...new ShoppingList(),
      id: this.editForm.get(['id'])!.value,
      total: this.editForm.get(['total'])!.value,
      nameShopList: this.editForm.get(['nameShopList'])!.value,
      group: this.editForm.get(['group'])!.value,
    };
  }
}
