import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'group',
        data: { pageTitle: 'Groups' },
        loadChildren: () => import('./Homies/group/group.module').then(m => m.HomiesGroupModule),
      },
      {
        path: 'user-name',
        data: { pageTitle: 'UserNames' },
        loadChildren: () => import('./Homies/user-name/user-name.module').then(m => m.HomiesUserNameModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
