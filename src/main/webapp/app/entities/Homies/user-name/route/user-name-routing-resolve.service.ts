import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserName, UserName } from '../user-name.model';
import { UserNameService } from '../service/user-name.service';

@Injectable({ providedIn: 'root' })
export class UserNameRoutingResolveService implements Resolve<IUserName> {
  constructor(protected service: UserNameService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserName> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userName: HttpResponse<UserName>) => {
          if (userName.body) {
            return of(userName.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserName());
  }
}
