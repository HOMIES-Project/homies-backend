import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserNameComponent } from './list/user-name.component';
import { UserNameDetailComponent } from './detail/user-name-detail.component';
import { UserNameUpdateComponent } from './update/user-name-update.component';
import { UserNameDeleteDialogComponent } from './delete/user-name-delete-dialog.component';
import { UserNameRoutingModule } from './route/user-name-routing.module';

@NgModule({
  imports: [SharedModule, UserNameRoutingModule],
  declarations: [UserNameComponent, UserNameDetailComponent, UserNameUpdateComponent, UserNameDeleteDialogComponent],
  entryComponents: [UserNameDeleteDialogComponent],
})
export class PruebaMicroservicioUserNameModule {}
