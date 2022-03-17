import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserData } from '../user-data.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-user-data-detail',
  templateUrl: './user-data-detail.component.html',
})
export class UserDataDetailComponent implements OnInit {
  userData: IUserData | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userData }) => {
      this.userData = userData;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
