import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISpending, Spending } from '../spending.model';

import { SpendingService } from './spending.service';

describe('Spending Service', () => {
  let service: SpendingService;
  let httpMock: HttpTestingController;
  let elemDefault: ISpending;
  let expectedResult: ISpending | ISpending[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpendingService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      payer: 0,
      nameCost: 'AAAAAAA',
      cost: 0,
      photoContentType: 'image/png',
      photo: 'AAAAAAA',
      descripcion: 'AAAAAAA',
      paid: 0,
      pending: 0,
      finished: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Spending', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Spending()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Spending', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          payer: 1,
          nameCost: 'BBBBBB',
          cost: 1,
          photo: 'BBBBBB',
          descripcion: 'BBBBBB',
          paid: 1,
          pending: 1,
          finished: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Spending', () => {
      const patchObject = Object.assign(
        {
          payer: 1,
          descripcion: 'BBBBBB',
          paid: 1,
          pending: 1,
        },
        new Spending()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Spending', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          payer: 1,
          nameCost: 'BBBBBB',
          cost: 1,
          photo: 'BBBBBB',
          descripcion: 'BBBBBB',
          paid: 1,
          pending: 1,
          finished: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Spending', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSpendingToCollectionIfMissing', () => {
      it('should add a Spending to an empty array', () => {
        const spending: ISpending = { id: 123 };
        expectedResult = service.addSpendingToCollectionIfMissing([], spending);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spending);
      });

      it('should not add a Spending to an array that contains it', () => {
        const spending: ISpending = { id: 123 };
        const spendingCollection: ISpending[] = [
          {
            ...spending,
          },
          { id: 456 },
        ];
        expectedResult = service.addSpendingToCollectionIfMissing(spendingCollection, spending);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Spending to an array that doesn't contain it", () => {
        const spending: ISpending = { id: 123 };
        const spendingCollection: ISpending[] = [{ id: 456 }];
        expectedResult = service.addSpendingToCollectionIfMissing(spendingCollection, spending);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spending);
      });

      it('should add only unique Spending to an array', () => {
        const spendingArray: ISpending[] = [{ id: 123 }, { id: 456 }, { id: 58184 }];
        const spendingCollection: ISpending[] = [{ id: 123 }];
        expectedResult = service.addSpendingToCollectionIfMissing(spendingCollection, ...spendingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const spending: ISpending = { id: 123 };
        const spending2: ISpending = { id: 456 };
        expectedResult = service.addSpendingToCollectionIfMissing([], spending, spending2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spending);
        expect(expectedResult).toContain(spending2);
      });

      it('should accept null and undefined values', () => {
        const spending: ISpending = { id: 123 };
        expectedResult = service.addSpendingToCollectionIfMissing([], null, spending, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spending);
      });

      it('should return initial array if no Spending is added', () => {
        const spendingCollection: ISpending[] = [{ id: 123 }];
        expectedResult = service.addSpendingToCollectionIfMissing(spendingCollection, undefined, null);
        expect(expectedResult).toEqual(spendingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
