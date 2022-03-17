import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TaskListComponent } from '../list/task-list.component';
import { TaskListDetailComponent } from '../detail/task-list-detail.component';
import { TaskListUpdateComponent } from '../update/task-list-update.component';
import { TaskListRoutingResolveService } from './task-list-routing-resolve.service';

const taskListRoute: Routes = [
  {
    path: '',
    component: TaskListComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TaskListDetailComponent,
    resolve: {
      taskList: TaskListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TaskListUpdateComponent,
    resolve: {
      taskList: TaskListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TaskListUpdateComponent,
    resolve: {
      taskList: TaskListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(taskListRoute)],
  exports: [RouterModule],
})
export class TaskListRoutingModule {}
