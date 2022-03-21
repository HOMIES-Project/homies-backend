import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SettingsListComponent } from '../list/settings-list.component';
import { SettingsListDetailComponent } from '../detail/settings-list-detail.component';
import { SettingsListUpdateComponent } from '../update/settings-list-update.component';
import { SettingsListRoutingResolveService } from './settings-list-routing-resolve.service';

const settingsListRoute: Routes = [
  {
    path: '',
    component: SettingsListComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SettingsListDetailComponent,
    resolve: {
      settingsList: SettingsListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SettingsListUpdateComponent,
    resolve: {
      settingsList: SettingsListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SettingsListUpdateComponent,
    resolve: {
      settingsList: SettingsListRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(settingsListRoute)],
  exports: [RouterModule],
})
export class SettingsListRoutingModule {}
