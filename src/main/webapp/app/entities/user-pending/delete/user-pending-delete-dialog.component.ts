import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserPending } from '../user-pending.model';
import { UserPendingService } from '../service/user-pending.service';

@Component({
  templateUrl: './user-pending-delete-dialog.component.html',
})
export class UserPendingDeleteDialogComponent {
  userPending?: IUserPending;

  constructor(protected userPendingService: UserPendingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userPendingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
