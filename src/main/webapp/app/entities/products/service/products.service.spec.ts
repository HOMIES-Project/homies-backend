import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IProducts, Products } from '../products.model';

import { ProductsService } from './products.service';

describe('Products Service', () => {
  let service: ProductsService;
  let httpMock: HttpTestingController;
  let elemDefault: IProducts;
  let expectedResult: IProducts | IProducts[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductsService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      price: 0,
      photoContentType: 'image/png',
      photo: 'AAAAAAA',
      units: 0,
      typeUnit: 'AAAAAAA',
      note: 'AAAAAAA',
      dataCreated: currentDate,
      shoppingDate: currentDate,
      purchased: false,
      userCreated: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dataCreated: currentDate.format(DATE_FORMAT),
          shoppingDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Products', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dataCreated: currentDate.format(DATE_FORMAT),
          shoppingDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataCreated: currentDate,
          shoppingDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Products()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Products', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          price: 1,
          photo: 'BBBBBB',
          units: 1,
          typeUnit: 'BBBBBB',
          note: 'BBBBBB',
          dataCreated: currentDate.format(DATE_FORMAT),
          shoppingDate: currentDate.format(DATE_FORMAT),
          purchased: true,
          userCreated: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataCreated: currentDate,
          shoppingDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Products', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          price: 1,
          typeUnit: 'BBBBBB',
          note: 'BBBBBB',
          dataCreated: currentDate.format(DATE_FORMAT),
          shoppingDate: currentDate.format(DATE_FORMAT),
        },
        new Products()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dataCreated: currentDate,
          shoppingDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Products', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          price: 1,
          photo: 'BBBBBB',
          units: 1,
          typeUnit: 'BBBBBB',
          note: 'BBBBBB',
          dataCreated: currentDate.format(DATE_FORMAT),
          shoppingDate: currentDate.format(DATE_FORMAT),
          purchased: true,
          userCreated: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataCreated: currentDate,
          shoppingDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Products', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProductsToCollectionIfMissing', () => {
      it('should add a Products to an empty array', () => {
        const products: IProducts = { id: 123 };
        expectedResult = service.addProductsToCollectionIfMissing([], products);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(products);
      });

      it('should not add a Products to an array that contains it', () => {
        const products: IProducts = { id: 123 };
        const productsCollection: IProducts[] = [
          {
            ...products,
          },
          { id: 456 },
        ];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, products);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Products to an array that doesn't contain it", () => {
        const products: IProducts = { id: 123 };
        const productsCollection: IProducts[] = [{ id: 456 }];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, products);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(products);
      });

      it('should add only unique Products to an array', () => {
        const productsArray: IProducts[] = [{ id: 123 }, { id: 456 }, { id: 67025 }];
        const productsCollection: IProducts[] = [{ id: 123 }];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, ...productsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const products: IProducts = { id: 123 };
        const products2: IProducts = { id: 456 };
        expectedResult = service.addProductsToCollectionIfMissing([], products, products2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(products);
        expect(expectedResult).toContain(products2);
      });

      it('should accept null and undefined values', () => {
        const products: IProducts = { id: 123 };
        expectedResult = service.addProductsToCollectionIfMissing([], null, products, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(products);
      });

      it('should return initial array if no Products is added', () => {
        const productsCollection: IProducts[] = [{ id: 123 }];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, undefined, null);
        expect(expectedResult).toEqual(productsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
