import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SettingsListComponent } from './list/settings-list.component';
import { SettingsListDetailComponent } from './detail/settings-list-detail.component';
import { SettingsListUpdateComponent } from './update/settings-list-update.component';
import { SettingsListDeleteDialogComponent } from './delete/settings-list-delete-dialog.component';
import { SettingsListRoutingModule } from './route/settings-list-routing.module';

@NgModule({
  imports: [SharedModule, SettingsListRoutingModule],
  declarations: [SettingsListComponent, SettingsListDetailComponent, SettingsListUpdateComponent, SettingsListDeleteDialogComponent],
  entryComponents: [SettingsListDeleteDialogComponent],
})
export class SettingsListModule {}
