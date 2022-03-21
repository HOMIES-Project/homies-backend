import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISettingsList } from '../settings-list.model';
import { SettingsListService } from '../service/settings-list.service';

@Component({
  templateUrl: './settings-list-delete-dialog.component.html',
})
export class SettingsListDeleteDialogComponent {
  settingsList?: ISettingsList;

  constructor(protected settingsListService: SettingsListService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.settingsListService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
