import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpending, getSpendingIdentifier } from '../spending.model';

export type EntityResponseType = HttpResponse<ISpending>;
export type EntityArrayResponseType = HttpResponse<ISpending[]>;

@Injectable({ providedIn: 'root' })
export class SpendingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/spendings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(spending: ISpending): Observable<EntityResponseType> {
    return this.http.post<ISpending>(this.resourceUrl, spending, { observe: 'response' });
  }

  update(spending: ISpending): Observable<EntityResponseType> {
    return this.http.put<ISpending>(`${this.resourceUrl}/${getSpendingIdentifier(spending) as number}`, spending, { observe: 'response' });
  }

  partialUpdate(spending: ISpending): Observable<EntityResponseType> {
    return this.http.patch<ISpending>(`${this.resourceUrl}/${getSpendingIdentifier(spending) as number}`, spending, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISpending>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpending[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSpendingToCollectionIfMissing(spendingCollection: ISpending[], ...spendingsToCheck: (ISpending | null | undefined)[]): ISpending[] {
    const spendings: ISpending[] = spendingsToCheck.filter(isPresent);
    if (spendings.length > 0) {
      const spendingCollectionIdentifiers = spendingCollection.map(spendingItem => getSpendingIdentifier(spendingItem)!);
      const spendingsToAdd = spendings.filter(spendingItem => {
        const spendingIdentifier = getSpendingIdentifier(spendingItem);
        if (spendingIdentifier == null || spendingCollectionIdentifiers.includes(spendingIdentifier)) {
          return false;
        }
        spendingCollectionIdentifiers.push(spendingIdentifier);
        return true;
      });
      return [...spendingsToAdd, ...spendingCollection];
    }
    return spendingCollection;
  }
}
