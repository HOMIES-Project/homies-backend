import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISpending } from '../spending.model';
import { SpendingService } from '../service/spending.service';

@Component({
  templateUrl: './spending-delete-dialog.component.html',
})
export class SpendingDeleteDialogComponent {
  spending?: ISpending;

  constructor(protected spendingService: SpendingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.spendingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
