import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserData, getUserDataIdentifier } from '../user-data.model';

export type EntityResponseType = HttpResponse<IUserData>;
export type EntityArrayResponseType = HttpResponse<IUserData[]>;

@Injectable({ providedIn: 'root' })
export class UserDataService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-data');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userData: IUserData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userData);
    return this.http
      .post<IUserData>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(userData: IUserData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userData);
    return this.http
      .put<IUserData>(`${this.resourceUrl}/${getUserDataIdentifier(userData) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(userData: IUserData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userData);
    return this.http
      .patch<IUserData>(`${this.resourceUrl}/${getUserDataIdentifier(userData) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IUserData>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUserData[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUserDataToCollectionIfMissing(userDataCollection: IUserData[], ...userDataToCheck: (IUserData | null | undefined)[]): IUserData[] {
    const userData: IUserData[] = userDataToCheck.filter(isPresent);
    if (userData.length > 0) {
      const userDataCollectionIdentifiers = userDataCollection.map(userDataItem => getUserDataIdentifier(userDataItem)!);
      const userDataToAdd = userData.filter(userDataItem => {
        const userDataIdentifier = getUserDataIdentifier(userDataItem);
        if (userDataIdentifier == null || userDataCollectionIdentifiers.includes(userDataIdentifier)) {
          return false;
        }
        userDataCollectionIdentifiers.push(userDataIdentifier);
        return true;
      });
      return [...userDataToAdd, ...userDataCollection];
    }
    return userDataCollection;
  }

  protected convertDateFromClient(userData: IUserData): IUserData {
    return Object.assign({}, userData, {
      birthDate: userData.birthDate?.isValid() ? userData.birthDate.format(DATE_FORMAT) : undefined,
      addDate: userData.addDate?.isValid() ? userData.addDate.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((userData: IUserData) => {
        userData.birthDate = userData.birthDate ? dayjs(userData.birthDate) : undefined;
        userData.addDate = userData.addDate ? dayjs(userData.addDate) : undefined;
      });
    }
    return res;
  }
}
