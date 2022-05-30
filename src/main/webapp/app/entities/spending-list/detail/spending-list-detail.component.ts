import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISpendingList } from '../spending-list.model';

@Component({
  selector: 'jhi-spending-list-detail',
  templateUrl: './spending-list-detail.component.html',
})
export class SpendingListDetailComponent implements OnInit {
  spendingList: ISpendingList | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spendingList }) => {
      this.spendingList = spendingList;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
