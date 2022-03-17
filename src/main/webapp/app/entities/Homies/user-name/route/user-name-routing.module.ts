import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserNameComponent } from '../list/user-name.component';
import { UserNameDetailComponent } from '../detail/user-name-detail.component';
import { UserNameUpdateComponent } from '../update/user-name-update.component';
import { UserNameRoutingResolveService } from './user-name-routing-resolve.service';

const userNameRoute: Routes = [
  {
    path: '',
    component: UserNameComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserNameDetailComponent,
    resolve: {
      userName: UserNameRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserNameUpdateComponent,
    resolve: {
      userName: UserNameRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserNameUpdateComponent,
    resolve: {
      userName: UserNameRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userNameRoute)],
  exports: [RouterModule],
})
export class UserNameRoutingModule {}
