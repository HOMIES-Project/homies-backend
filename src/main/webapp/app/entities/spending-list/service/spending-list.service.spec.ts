import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISpendingList, SpendingList } from '../spending-list.model';

import { SpendingListService } from './spending-list.service';

describe('SpendingList Service', () => {
  let service: SpendingListService;
  let httpMock: HttpTestingController;
  let elemDefault: ISpendingList;
  let expectedResult: ISpendingList | ISpendingList[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpendingListService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      total: 0,
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

    it('should create a SpendingList', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new SpendingList()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SpendingList', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          total: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SpendingList', () => {
      const patchObject = Object.assign({}, new SpendingList());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SpendingList', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          total: 1,
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

    it('should delete a SpendingList', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSpendingListToCollectionIfMissing', () => {
      it('should add a SpendingList to an empty array', () => {
        const spendingList: ISpendingList = { id: 123 };
        expectedResult = service.addSpendingListToCollectionIfMissing([], spendingList);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spendingList);
      });

      it('should not add a SpendingList to an array that contains it', () => {
        const spendingList: ISpendingList = { id: 123 };
        const spendingListCollection: ISpendingList[] = [
          {
            ...spendingList,
          },
          { id: 456 },
        ];
        expectedResult = service.addSpendingListToCollectionIfMissing(spendingListCollection, spendingList);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SpendingList to an array that doesn't contain it", () => {
        const spendingList: ISpendingList = { id: 123 };
        const spendingListCollection: ISpendingList[] = [{ id: 456 }];
        expectedResult = service.addSpendingListToCollectionIfMissing(spendingListCollection, spendingList);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spendingList);
      });

      it('should add only unique SpendingList to an array', () => {
        const spendingListArray: ISpendingList[] = [{ id: 123 }, { id: 456 }, { id: 91664 }];
        const spendingListCollection: ISpendingList[] = [{ id: 123 }];
        expectedResult = service.addSpendingListToCollectionIfMissing(spendingListCollection, ...spendingListArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const spendingList: ISpendingList = { id: 123 };
        const spendingList2: ISpendingList = { id: 456 };
        expectedResult = service.addSpendingListToCollectionIfMissing([], spendingList, spendingList2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spendingList);
        expect(expectedResult).toContain(spendingList2);
      });

      it('should accept null and undefined values', () => {
        const spendingList: ISpendingList = { id: 123 };
        expectedResult = service.addSpendingListToCollectionIfMissing([], null, spendingList, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spendingList);
      });

      it('should return initial array if no SpendingList is added', () => {
        const spendingListCollection: ISpendingList[] = [{ id: 123 }];
        expectedResult = service.addSpendingListToCollectionIfMissing(spendingListCollection, undefined, null);
        expect(expectedResult).toEqual(spendingListCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
