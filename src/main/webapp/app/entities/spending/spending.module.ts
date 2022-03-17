import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SpendingComponent } from './list/spending.component';
import { SpendingDetailComponent } from './detail/spending-detail.component';
import { SpendingUpdateComponent } from './update/spending-update.component';
import { SpendingDeleteDialogComponent } from './delete/spending-delete-dialog.component';
import { SpendingRoutingModule } from './route/spending-routing.module';

@NgModule({
  imports: [SharedModule, SpendingRoutingModule],
  declarations: [SpendingComponent, SpendingDetailComponent, SpendingUpdateComponent, SpendingDeleteDialogComponent],
  entryComponents: [SpendingDeleteDialogComponent],
})
export class SpendingModule {}
