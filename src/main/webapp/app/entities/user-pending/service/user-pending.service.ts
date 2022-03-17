import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserPending, getUserPendingIdentifier } from '../user-pending.model';

export type EntityResponseType = HttpResponse<IUserPending>;
export type EntityArrayResponseType = HttpResponse<IUserPending[]>;

@Injectable({ providedIn: 'root' })
export class UserPendingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-pendings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userPending: IUserPending): Observable<EntityResponseType> {
    return this.http.post<IUserPending>(this.resourceUrl, userPending, { observe: 'response' });
  }

  update(userPending: IUserPending): Observable<EntityResponseType> {
    return this.http.put<IUserPending>(`${this.resourceUrl}/${getUserPendingIdentifier(userPending) as number}`, userPending, {
      observe: 'response',
    });
  }

  partialUpdate(userPending: IUserPending): Observable<EntityResponseType> {
    return this.http.patch<IUserPending>(`${this.resourceUrl}/${getUserPendingIdentifier(userPending) as number}`, userPending, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserPending>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserPending[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUserPendingToCollectionIfMissing(
    userPendingCollection: IUserPending[],
    ...userPendingsToCheck: (IUserPending | null | undefined)[]
  ): IUserPending[] {
    const userPendings: IUserPending[] = userPendingsToCheck.filter(isPresent);
    if (userPendings.length > 0) {
      const userPendingCollectionIdentifiers = userPendingCollection.map(userPendingItem => getUserPendingIdentifier(userPendingItem)!);
      const userPendingsToAdd = userPendings.filter(userPendingItem => {
        const userPendingIdentifier = getUserPendingIdentifier(userPendingItem);
        if (userPendingIdentifier == null || userPendingCollectionIdentifiers.includes(userPendingIdentifier)) {
          return false;
        }
        userPendingCollectionIdentifiers.push(userPendingIdentifier);
        return true;
      });
      return [...userPendingsToAdd, ...userPendingCollection];
    }
    return userPendingCollection;
  }
}
