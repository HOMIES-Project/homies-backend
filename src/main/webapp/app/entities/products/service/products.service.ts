import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProducts, getProductsIdentifier } from '../products.model';

export type EntityResponseType = HttpResponse<IProducts>;
export type EntityArrayResponseType = HttpResponse<IProducts[]>;

@Injectable({ providedIn: 'root' })
export class ProductsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(products: IProducts): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(products);
    return this.http
      .post<IProducts>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(products: IProducts): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(products);
    return this.http
      .put<IProducts>(`${this.resourceUrl}/${getProductsIdentifier(products) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(products: IProducts): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(products);
    return this.http
      .patch<IProducts>(`${this.resourceUrl}/${getProductsIdentifier(products) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IProducts>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProducts[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProductsToCollectionIfMissing(productsCollection: IProducts[], ...productsToCheck: (IProducts | null | undefined)[]): IProducts[] {
    const products: IProducts[] = productsToCheck.filter(isPresent);
    if (products.length > 0) {
      const productsCollectionIdentifiers = productsCollection.map(productsItem => getProductsIdentifier(productsItem)!);
      const productsToAdd = products.filter(productsItem => {
        const productsIdentifier = getProductsIdentifier(productsItem);
        if (productsIdentifier == null || productsCollectionIdentifiers.includes(productsIdentifier)) {
          return false;
        }
        productsCollectionIdentifiers.push(productsIdentifier);
        return true;
      });
      return [...productsToAdd, ...productsCollection];
    }
    return productsCollection;
  }

  protected convertDateFromClient(products: IProducts): IProducts {
    return Object.assign({}, products, {
      dataCreated: products.dataCreated?.isValid() ? products.dataCreated.format(DATE_FORMAT) : undefined,
      shoppingDate: products.shoppingDate?.isValid() ? products.shoppingDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dataCreated = res.body.dataCreated ? dayjs(res.body.dataCreated) : undefined;
      res.body.shoppingDate = res.body.shoppingDate ? dayjs(res.body.shoppingDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((products: IProducts) => {
        products.dataCreated = products.dataCreated ? dayjs(products.dataCreated) : undefined;
        products.shoppingDate = products.shoppingDate ? dayjs(products.shoppingDate) : undefined;
      });
    }
    return res;
  }
}
