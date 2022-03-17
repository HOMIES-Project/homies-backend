import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISpendingList } from '../spending-list.model';
import { SpendingListService } from '../service/spending-list.service';

@Component({
  templateUrl: './spending-list-delete-dialog.component.html',
})
export class SpendingListDeleteDialogComponent {
  spendingList?: ISpendingList;

  constructor(protected spendingListService: SpendingListService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.spendingListService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
