import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGroup, getGroupIdentifier } from '../group.model';

export type EntityResponseType = HttpResponse<IGroup>;
export type EntityArrayResponseType = HttpResponse<IGroup[]>;

@Injectable({ providedIn: 'root' })
export class GroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(group: IGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(group);
    return this.http
      .post<IGroup>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(group: IGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(group);
    return this.http
      .put<IGroup>(`${this.resourceUrl}/${getGroupIdentifier(group) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(group: IGroup): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(group);
    return this.http
      .patch<IGroup>(`${this.resourceUrl}/${getGroupIdentifier(group) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IGroup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGroupToCollectionIfMissing(groupCollection: IGroup[], ...groupsToCheck: (IGroup | null | undefined)[]): IGroup[] {
    const groups: IGroup[] = groupsToCheck.filter(isPresent);
    if (groups.length > 0) {
      const groupCollectionIdentifiers = groupCollection.map(groupItem => getGroupIdentifier(groupItem)!);
      const groupsToAdd = groups.filter(groupItem => {
        const groupIdentifier = getGroupIdentifier(groupItem);
        if (groupIdentifier == null || groupCollectionIdentifiers.includes(groupIdentifier)) {
          return false;
        }
        groupCollectionIdentifiers.push(groupIdentifier);
        return true;
      });
      return [...groupsToAdd, ...groupCollection];
    }
    return groupCollection;
  }

  protected convertDateFromClient(group: IGroup): IGroup {
    return Object.assign({}, group, {
      addGroupDate: group.addGroupDate?.isValid() ? group.addGroupDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.addGroupDate = res.body.addGroupDate ? dayjs(res.body.addGroupDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((group: IGroup) => {
        group.addGroupDate = group.addGroupDate ? dayjs(group.addGroupDate) : undefined;
      });
    }
    return res;
  }
}
