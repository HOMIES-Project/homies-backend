import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUserName, UserName } from '../user-name.model';
import { UserNameService } from '../service/user-name.service';
import { IUserData } from 'app/entities/PruebaMicroservicio/user-data/user-data.model';
import { UserDataService } from 'app/entities/PruebaMicroservicio/user-data/service/user-data.service';

@Component({
  selector: 'jhi-user-name-update',
  templateUrl: './user-name-update.component.html',
})
export class UserNameUpdateComponent implements OnInit {
  isSaving = false;

  userDataCollection: IUserData[] = [];

  editForm = this.fb.group({
    id: [],
    user_name: [null, [Validators.required, Validators.minLength(10), Validators.maxLength(50)]],
    password: [null, [Validators.required, Validators.minLength(8), Validators.maxLength(16)]],
    token: [],
    userData: [null, Validators.required],
  });

  constructor(
    protected userNameService: UserNameService,
    protected userDataService: UserDataService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userName }) => {
      this.updateForm(userName);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userName = this.createFromForm();
    if (userName.id !== undefined) {
      this.subscribeToSaveResponse(this.userNameService.update(userName));
    } else {
      this.subscribeToSaveResponse(this.userNameService.create(userName));
    }
  }

  trackUserDataById(index: number, item: IUserData): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserName>>): void {
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

  protected updateForm(userName: IUserName): void {
    this.editForm.patchValue({
      id: userName.id,
      user_name: userName.user_name,
      password: userName.password,
      token: userName.token,
      userData: userName.userData,
    });

    this.userDataCollection = this.userDataService.addUserDataToCollectionIfMissing(this.userDataCollection, userName.userData);
  }

  protected loadRelationshipsOptions(): void {
    this.userDataService
      .query({ 'userNameId.specified': 'false' })
      .pipe(map((res: HttpResponse<IUserData[]>) => res.body ?? []))
      .pipe(
        map((userData: IUserData[]) =>
          this.userDataService.addUserDataToCollectionIfMissing(userData, this.editForm.get('userData')!.value)
        )
      )
      .subscribe((userData: IUserData[]) => (this.userDataCollection = userData));
  }

  protected createFromForm(): IUserName {
    return {
      ...new UserName(),
      id: this.editForm.get(['id'])!.value,
      user_name: this.editForm.get(['user_name'])!.value,
      password: this.editForm.get(['password'])!.value,
      token: this.editForm.get(['token'])!.value,
      userData: this.editForm.get(['userData'])!.value,
    };
  }
}
