import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpendingList, getSpendingListIdentifier } from '../spending-list.model';

export type EntityResponseType = HttpResponse<ISpendingList>;
export type EntityArrayResponseType = HttpResponse<ISpendingList[]>;

@Injectable({ providedIn: 'root' })
export class SpendingListService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/spending-lists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(spendingList: ISpendingList): Observable<EntityResponseType> {
    return this.http.post<ISpendingList>(this.resourceUrl, spendingList, { observe: 'response' });
  }

  update(spendingList: ISpendingList): Observable<EntityResponseType> {
    return this.http.put<ISpendingList>(`${this.resourceUrl}/${getSpendingListIdentifier(spendingList) as number}`, spendingList, {
      observe: 'response',
    });
  }

  partialUpdate(spendingList: ISpendingList): Observable<EntityResponseType> {
    return this.http.patch<ISpendingList>(`${this.resourceUrl}/${getSpendingListIdentifier(spendingList) as number}`, spendingList, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISpendingList>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpendingList[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSpendingListToCollectionIfMissing(
    spendingListCollection: ISpendingList[],
    ...spendingListsToCheck: (ISpendingList | null | undefined)[]
  ): ISpendingList[] {
    const spendingLists: ISpendingList[] = spendingListsToCheck.filter(isPresent);
    if (spendingLists.length > 0) {
      const spendingListCollectionIdentifiers = spendingListCollection.map(
        spendingListItem => getSpendingListIdentifier(spendingListItem)!
      );
      const spendingListsToAdd = spendingLists.filter(spendingListItem => {
        const spendingListIdentifier = getSpendingListIdentifier(spendingListItem);
        if (spendingListIdentifier == null || spendingListCollectionIdentifiers.includes(spendingListIdentifier)) {
          return false;
        }
        spendingListCollectionIdentifiers.push(spendingListIdentifier);
        return true;
      });
      return [...spendingListsToAdd, ...spendingListCollection];
    }
    return spendingListCollection;
  }
}
