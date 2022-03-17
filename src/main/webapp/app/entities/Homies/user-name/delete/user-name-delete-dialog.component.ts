import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserName } from '../user-name.model';
import { UserNameService } from '../service/user-name.service';

@Component({
  templateUrl: './user-name-delete-dialog.component.html',
})
export class UserNameDeleteDialogComponent {
  userName?: IUserName;

  constructor(protected userNameService: UserNameService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userNameService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
