import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'user-name',
        data: { pageTitle: 'UserNames' },
        loadChildren: () => import('./PruebaMicroservicio/user-name/user-name.module').then(m => m.PruebaMicroservicioUserNameModule),
      },
      {
        path: 'user-data',
        data: { pageTitle: 'UserData' },
        loadChildren: () => import('./PruebaMicroservicio/user-data/user-data.module').then(m => m.PruebaMicroservicioUserDataModule),
      },
      {
        path: 'group',
        data: { pageTitle: 'Groups' },
        loadChildren: () => import('./PruebaMicroservicio/group/group.module').then(m => m.PruebaMicroservicioGroupModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
