import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpending, Spending } from '../spending.model';
import { SpendingService } from '../service/spending.service';

@Injectable({ providedIn: 'root' })
export class SpendingRoutingResolveService implements Resolve<ISpending> {
  constructor(protected service: SpendingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISpending> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((spending: HttpResponse<Spending>) => {
          if (spending.body) {
            return of(spending.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Spending());
  }
}
