import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskList, TaskList } from '../task-list.model';
import { TaskListService } from '../service/task-list.service';

@Injectable({ providedIn: 'root' })
export class TaskListRoutingResolveService implements Resolve<ITaskList> {
  constructor(protected service: TaskListService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITaskList> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((taskList: HttpResponse<TaskList>) => {
          if (taskList.body) {
            return of(taskList.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TaskList());
  }
}
