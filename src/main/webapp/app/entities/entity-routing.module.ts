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
        path: 'products',
        data: { pageTitle: 'Products' },
        loadChildren: () => import('./products/products.module').then(m => m.ProductsModule),
      },
      {
        path: 'task',
        data: { pageTitle: 'Tasks' },
        loadChildren: () => import('./task/task.module').then(m => m.TaskModule),
      },
      {
        path: 'task-list',
        data: { pageTitle: 'TaskLists' },
        loadChildren: () => import('./task-list/task-list.module').then(m => m.TaskListModule),
      },
      {
        path: 'shopping-list',
        data: { pageTitle: 'ShoppingLists' },
        loadChildren: () => import('./shopping-list/shopping-list.module').then(m => m.ShoppingListModule),
      },
      {
        path: 'spending',
        data: { pageTitle: 'Spendings' },
        loadChildren: () => import('./spending/spending.module').then(m => m.SpendingModule),
      },
      {
        path: 'user-pending',
        data: { pageTitle: 'UserPendings' },
        loadChildren: () => import('./user-pending/user-pending.module').then(m => m.UserPendingModule),
      },
      {
        path: 'spending-list',
        data: { pageTitle: 'SpendingLists' },
        loadChildren: () => import('./spending-list/spending-list.module').then(m => m.SpendingListModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
