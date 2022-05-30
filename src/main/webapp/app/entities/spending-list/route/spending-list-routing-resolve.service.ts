import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpendingList, SpendingList } from '../spending-list.model';
import { SpendingListService } from '../service/spending-list.service';

@Injectable({ providedIn: 'root' })
export class SpendingListRoutingResolveService implements Resolve<ISpendingList> {
  constructor(protected service: SpendingListService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISpendingList> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((spendingList: HttpResponse<SpendingList>) => {
          if (spendingList.body) {
            return of(spendingList.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SpendingList());
  }
}
