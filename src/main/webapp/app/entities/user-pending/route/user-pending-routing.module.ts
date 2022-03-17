import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserPendingComponent } from '../list/user-pending.component';
import { UserPendingDetailComponent } from '../detail/user-pending-detail.component';
import { UserPendingUpdateComponent } from '../update/user-pending-update.component';
import { UserPendingRoutingResolveService } from './user-pending-routing-resolve.service';

const userPendingRoute: Routes = [
  {
    path: '',
    component: UserPendingComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserPendingDetailComponent,
    resolve: {
      userPending: UserPendingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserPendingUpdateComponent,
    resolve: {
      userPending: UserPendingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserPendingUpdateComponent,
    resolve: {
      userPending: UserPendingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userPendingRoute)],
  exports: [RouterModule],
})
export class UserPendingRoutingModule {}
