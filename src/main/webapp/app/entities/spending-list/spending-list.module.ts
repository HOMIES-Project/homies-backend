import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SpendingListComponent } from './list/spending-list.component';
import { SpendingListDetailComponent } from './detail/spending-list-detail.component';
import { SpendingListUpdateComponent } from './update/spending-list-update.component';
import { SpendingListDeleteDialogComponent } from './delete/spending-list-delete-dialog.component';
import { SpendingListRoutingModule } from './route/spending-list-routing.module';

@NgModule({
  imports: [SharedModule, SpendingListRoutingModule],
  declarations: [SpendingListComponent, SpendingListDetailComponent, SpendingListUpdateComponent, SpendingListDeleteDialogComponent],
  entryComponents: [SpendingListDeleteDialogComponent],
})
export class SpendingListModule {}
