import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserPending } from '../user-pending.model';

@Component({
  selector: 'jhi-user-pending-detail',
  templateUrl: './user-pending-detail.component.html',
})
export class UserPendingDetailComponent implements OnInit {
  userPending: IUserPending | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userPending }) => {
      this.userPending = userPending;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
