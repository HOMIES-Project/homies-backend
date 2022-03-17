import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpendingComponent } from '../list/spending.component';
import { SpendingDetailComponent } from '../detail/spending-detail.component';
import { SpendingUpdateComponent } from '../update/spending-update.component';
import { SpendingRoutingResolveService } from './spending-routing-resolve.service';

const spendingRoute: Routes = [
  {
    path: '',
    component: SpendingComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpendingDetailComponent,
    resolve: {
      spending: SpendingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpendingUpdateComponent,
    resolve: {
      spending: SpendingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpendingUpdateComponent,
    resolve: {
      spending: SpendingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(spendingRoute)],
  exports: [RouterModule],
})
export class SpendingRoutingModule {}
