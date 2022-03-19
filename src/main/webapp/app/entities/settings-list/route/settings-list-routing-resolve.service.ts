import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISettingsList, SettingsList } from '../settings-list.model';
import { SettingsListService } from '../service/settings-list.service';

@Injectable({ providedIn: 'root' })
export class SettingsListRoutingResolveService implements Resolve<ISettingsList> {
  constructor(protected service: SettingsListService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISettingsList> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((settingsList: HttpResponse<SettingsList>) => {
          if (settingsList.body) {
            return of(settingsList.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SettingsList());
  }
}
