import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserPendingComponent } from './list/user-pending.component';
import { UserPendingDetailComponent } from './detail/user-pending-detail.component';
import { UserPendingUpdateComponent } from './update/user-pending-update.component';
import { UserPendingDeleteDialogComponent } from './delete/user-pending-delete-dialog.component';
import { UserPendingRoutingModule } from './route/user-pending-routing.module';

@NgModule({
  imports: [SharedModule, UserPendingRoutingModule],
  declarations: [UserPendingComponent, UserPendingDetailComponent, UserPendingUpdateComponent, UserPendingDeleteDialogComponent],
  entryComponents: [UserPendingDeleteDialogComponent],
})
export class UserPendingModule {}
