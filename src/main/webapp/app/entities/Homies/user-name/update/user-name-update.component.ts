import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUserName, UserName } from '../user-name.model';
import { UserNameService } from '../service/user-name.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-user-name-update',
  templateUrl: './user-name-update.component.html',
})
export class UserNameUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    surname: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    photo: [],
    photoContentType: [],
    phone: [null, [Validators.minLength(6), Validators.maxLength(50)]],
    premium: [null, [Validators.required]],
    birthDate: [],
    addDate: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected userNameService: UserNameService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userName }) => {
      this.updateForm(userName);
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
    const userName = this.createFromForm();
    if (userName.id !== undefined) {
      this.subscribeToSaveResponse(this.userNameService.update(userName));
    } else {
      this.subscribeToSaveResponse(this.userNameService.create(userName));
    }
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
      name: userName.name,
      surname: userName.surname,
      photo: userName.photo,
      photoContentType: userName.photoContentType,
      phone: userName.phone,
      premium: userName.premium,
      birthDate: userName.birthDate,
      addDate: userName.addDate,
    });
  }

  protected createFromForm(): IUserName {
    return {
      ...new UserName(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      surname: this.editForm.get(['surname'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      premium: this.editForm.get(['premium'])!.value,
      birthDate: this.editForm.get(['birthDate'])!.value,
      addDate: this.editForm.get(['addDate'])!.value,
    };
  }
}
