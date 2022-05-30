import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserPending, UserPending } from '../user-pending.model';

import { UserPendingService } from './user-pending.service';

describe('UserPending Service', () => {
  let service: UserPendingService;
  let httpMock: HttpTestingController;
  let elemDefault: IUserPending;
  let expectedResult: IUserPending | IUserPending[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserPendingService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      pending: 0,
      paid: false,
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

    it('should create a UserPending', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new UserPending()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserPending', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          pending: 1,
          paid: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserPending', () => {
      const patchObject = Object.assign(
        {
          pending: 1,
        },
        new UserPending()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserPending', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          pending: 1,
          paid: true,
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

    it('should delete a UserPending', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUserPendingToCollectionIfMissing', () => {
      it('should add a UserPending to an empty array', () => {
        const userPending: IUserPending = { id: 123 };
        expectedResult = service.addUserPendingToCollectionIfMissing([], userPending);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userPending);
      });

      it('should not add a UserPending to an array that contains it', () => {
        const userPending: IUserPending = { id: 123 };
        const userPendingCollection: IUserPending[] = [
          {
            ...userPending,
          },
          { id: 456 },
        ];
        expectedResult = service.addUserPendingToCollectionIfMissing(userPendingCollection, userPending);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserPending to an array that doesn't contain it", () => {
        const userPending: IUserPending = { id: 123 };
        const userPendingCollection: IUserPending[] = [{ id: 456 }];
        expectedResult = service.addUserPendingToCollectionIfMissing(userPendingCollection, userPending);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userPending);
      });

      it('should add only unique UserPending to an array', () => {
        const userPendingArray: IUserPending[] = [{ id: 123 }, { id: 456 }, { id: 79562 }];
        const userPendingCollection: IUserPending[] = [{ id: 123 }];
        expectedResult = service.addUserPendingToCollectionIfMissing(userPendingCollection, ...userPendingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userPending: IUserPending = { id: 123 };
        const userPending2: IUserPending = { id: 456 };
        expectedResult = service.addUserPendingToCollectionIfMissing([], userPending, userPending2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userPending);
        expect(expectedResult).toContain(userPending2);
      });

      it('should accept null and undefined values', () => {
        const userPending: IUserPending = { id: 123 };
        expectedResult = service.addUserPendingToCollectionIfMissing([], null, userPending, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userPending);
      });

      it('should return initial array if no UserPending is added', () => {
        const userPendingCollection: IUserPending[] = [{ id: 123 }];
        expectedResult = service.addUserPendingToCollectionIfMissing(userPendingCollection, undefined, null);
        expect(expectedResult).toEqual(userPendingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
