import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
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
    const copy = this.convertDateFromClient(userName);
    return this.http
      .post<IUserName>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(userName: IUserName): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userName);
    return this.http
      .put<IUserName>(`${this.resourceUrl}/${getUserNameIdentifier(userName) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(userName: IUserName): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userName);
    return this.http
      .patch<IUserName>(`${this.resourceUrl}/${getUserNameIdentifier(userName) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IUserName>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUserName[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
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

  protected convertDateFromClient(userName: IUserName): IUserName {
    return Object.assign({}, userName, {
      birthDate: userName.birthDate?.isValid() ? userName.birthDate.format(DATE_FORMAT) : undefined,
      addDate: userName.addDate?.isValid() ? userName.addDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.birthDate = res.body.birthDate ? dayjs(res.body.birthDate) : undefined;
      res.body.addDate = res.body.addDate ? dayjs(res.body.addDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((userName: IUserName) => {
        userName.birthDate = userName.birthDate ? dayjs(userName.birthDate) : undefined;
        userName.addDate = userName.addDate ? dayjs(userName.addDate) : undefined;
      });
    }
    return res;
  }
}
