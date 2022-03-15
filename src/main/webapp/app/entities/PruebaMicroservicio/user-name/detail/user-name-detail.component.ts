import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserName } from '../user-name.model';

@Component({
  selector: 'jhi-user-name-detail',
  templateUrl: './user-name-detail.component.html',
})
export class UserNameDetailComponent implements OnInit {
  userName: IUserName | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userName }) => {
      this.userName = userName;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
