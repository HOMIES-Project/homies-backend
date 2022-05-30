import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpendingListComponent } from '../list/spending-list.component';
import { SpendingListDetailComponent } from '../detail/spending-list-detail.component';
import { SpendingListUpdateComponent } from '../update/spending-list-update.component';
import { SpendingListRoutingResolveService } from './spending-list-routing-resolve.service';

const spendingListRoute: Routes = [
  {
    path: '',
    component: SpendingListComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpendingListDetailComponent,
    resolve: {
      spendingList: SpendingListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpendingListUpdateComponent,
    resolve: {
      spendingList: SpendingListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpendingListUpdateComponent,
    resolve: {
      spendingList: SpendingListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(spendingListRoute)],
  exports: [RouterModule],
})
export class SpendingListRoutingModule {}
