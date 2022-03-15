import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserName, getUserNameIdentifier } from '../user-name.model';

export type EntityResponseType = HttpResponse<IUserName>;
export type EntityArrayResponseType = HttpResponse<IUserName[]>;

@Injectable({ providedIn: 'root' })
export class UserNameService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-names');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userName: IUserName): Observable<EntityResponseType> {
    return this.http.post<IUserName>(this.resourceUrl, userName, { observe: 'response' });
  }

  update(userName: IUserName): Observable<EntityResponseType> {
    return this.http.put<IUserName>(`${this.resourceUrl}/${getUserNameIdentifier(userName) as number}`, userName, { observe: 'response' });
  }

  partialUpdate(userName: IUserName): Observable<EntityResponseType> {
    return this.http.patch<IUserName>(`${this.resourceUrl}/${getUserNameIdentifier(userName) as number}`, userName, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserName>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserName[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUserNameToCollectionIfMissing(userNameCollection: IUserName[], ...userNamesToCheck: (IUserName | null | undefined)[]): IUserName[] {
    const userNames: IUserName[] = userNamesToCheck.filter(isPresent);
    if (userNames.length > 0) {
      const userNameCollectionIdentifiers = userNameCollection.map(userNameItem => getUserNameIdentifier(userNameItem)!);
      const userNamesToAdd = userNames.filter(userNameItem => {
        const userNameIdentifier = getUserNameIdentifier(userNameItem);
        if (userNameIdentifier == null || userNameCollectionIdentifiers.includes(userNameIdentifier)) {
          return false;
        }
        userNameCollectionIdentifiers.push(userNameIdentifier);
        return true;
      });
      return [...userNamesToAdd, ...userNameCollection];
    }
    return userNameCollection;
  }
}
