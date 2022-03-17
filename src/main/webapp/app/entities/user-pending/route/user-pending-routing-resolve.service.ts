import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserPending, UserPending } from '../user-pending.model';
import { UserPendingService } from '../service/user-pending.service';

@Injectable({ providedIn: 'root' })
export class UserPendingRoutingResolveService implements Resolve<IUserPending> {
  constructor(protected service: UserPendingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserPending> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userPending: HttpResponse<UserPending>) => {
          if (userPending.body) {
            return of(userPending.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserPending());
  }
}
