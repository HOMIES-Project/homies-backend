import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISettingsList, SettingsList } from '../settings-list.model';
import { SettingsListService } from '../service/settings-list.service';
import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { SpendingListService } from 'app/entities/spending-list/service/spending-list.service';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';

@Component({
  selector: 'jhi-settings-list-update',
  templateUrl: './settings-list-update.component.html',
})
export class SettingsListUpdateComponent implements OnInit {
  isSaving = false;

  spendingListsSharedCollection: ISpendingList[] = [];
  groupsCollection: IGroup[] = [];

  editForm = this.fb.group({
    id: [],
    settingOne: [],
    settingTwo: [],
    settingThree: [],
    settingFour: [],
    settingFive: [],
    settingSix: [],
    settingSeven: [],
    spendingList: [],
    group: [],
  });

  constructor(
    protected settingsListService: SettingsListService,
    protected spendingListService: SpendingListService,
    protected groupService: GroupService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ settingsList }) => {
      this.updateForm(settingsList);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const settingsList = this.createFromForm();
    if (settingsList.id !== undefined) {
      this.subscribeToSaveResponse(this.settingsListService.update(settingsList));
    } else {
      this.subscribeToSaveResponse(this.settingsListService.create(settingsList));
    }
  }

  trackSpendingListById(index: number, item: ISpendingList): number {
    return item.id!;
  }

  trackGroupById(index: number, item: IGroup): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISettingsList>>): void {
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

  protected updateForm(settingsList: ISettingsList): void {
    this.editForm.patchValue({
      id: settingsList.id,
      settingOne: settingsList.settingOne,
      settingTwo: settingsList.settingTwo,
      settingThree: settingsList.settingThree,
      settingFour: settingsList.settingFour,
      settingFive: settingsList.settingFive,
      settingSix: settingsList.settingSix,
      settingSeven: settingsList.settingSeven,
      spendingList: settingsList.spendingList,
      group: settingsList.group,
    });

    this.spendingListsSharedCollection = this.spendingListService.addSpendingListToCollectionIfMissing(
      this.spendingListsSharedCollection,
      settingsList.spendingList
    );
    this.groupsCollection = this.groupService.addGroupToCollectionIfMissing(this.groupsCollection, settingsList.group);
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

    this.groupService
      .query({ 'settingsListId.specified': 'false' })
      .pipe(map((res: HttpResponse<IGroup[]>) => res.body ?? []))
      .pipe(map((groups: IGroup[]) => this.groupService.addGroupToCollectionIfMissing(groups, this.editForm.get('group')!.value)))
      .subscribe((groups: IGroup[]) => (this.groupsCollection = groups));
  }

  protected createFromForm(): ISettingsList {
    return {
      ...new SettingsList(),
      id: this.editForm.get(['id'])!.value,
      settingOne: this.editForm.get(['settingOne'])!.value,
      settingTwo: this.editForm.get(['settingTwo'])!.value,
      settingThree: this.editForm.get(['settingThree'])!.value,
      settingFour: this.editForm.get(['settingFour'])!.value,
      settingFive: this.editForm.get(['settingFive'])!.value,
      settingSix: this.editForm.get(['settingSix'])!.value,
      settingSeven: this.editForm.get(['settingSeven'])!.value,
      spendingList: this.editForm.get(['spendingList'])!.value,
      group: this.editForm.get(['group'])!.value,
    };
  }
}
