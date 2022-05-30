import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TaskListComponent } from './list/task-list.component';
import { TaskListDetailComponent } from './detail/task-list-detail.component';
import { TaskListUpdateComponent } from './update/task-list-update.component';
import { TaskListDeleteDialogComponent } from './delete/task-list-delete-dialog.component';
import { TaskListRoutingModule } from './route/task-list-routing.module';

@NgModule({
  imports: [SharedModule, TaskListRoutingModule],
  declarations: [TaskListComponent, TaskListDetailComponent, TaskListUpdateComponent, TaskListDeleteDialogComponent],
  entryComponents: [TaskListDeleteDialogComponent],
})
export class TaskListModule {}
