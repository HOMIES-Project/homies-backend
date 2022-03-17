import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IUserData, UserData } from '../user-data.model';

import { UserDataService } from './user-data.service';

describe('UserData Service', () => {
  let service: UserDataService;
  let httpMock: HttpTestingController;
  let elemDefault: IUserData;
  let expectedResult: IUserData | IUserData[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserDataService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      photoContentType: 'image/png',
      photo: 'AAAAAAA',
      phone: 'AAAAAAA',
      premium: false,
      birthDate: currentDate,
      addDate: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          birthDate: currentDate.format(DATE_FORMAT),
          addDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a UserData', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          birthDate: currentDate.format(DATE_FORMAT),
          addDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          birthDate: currentDate,
          addDate: currentDate,
        },
        returnedFromService
      );

      service.create(new UserData()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserData', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          photo: 'BBBBBB',
          phone: 'BBBBBB',
          premium: true,
          birthDate: currentDate.format(DATE_FORMAT),
          addDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          birthDate: currentDate,
          addDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserData', () => {
      const patchObject = Object.assign(
        {
          premium: true,
        },
        new UserData()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          birthDate: currentDate,
          addDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserData', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          photo: 'BBBBBB',
          phone: 'BBBBBB',
          premium: true,
          birthDate: currentDate.format(DATE_FORMAT),
          addDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          birthDate: currentDate,
          addDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a UserData', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUserDataToCollectionIfMissing', () => {
      it('should add a UserData to an empty array', () => {
        const userData: IUserData = { id: 123 };
        expectedResult = service.addUserDataToCollectionIfMissing([], userData);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userData);
      });

      it('should not add a UserData to an array that contains it', () => {
        const userData: IUserData = { id: 123 };
        const userDataCollection: IUserData[] = [
          {
            ...userData,
          },
          { id: 456 },
        ];
        expectedResult = service.addUserDataToCollectionIfMissing(userDataCollection, userData);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserData to an array that doesn't contain it", () => {
        const userData: IUserData = { id: 123 };
        const userDataCollection: IUserData[] = [{ id: 456 }];
        expectedResult = service.addUserDataToCollectionIfMissing(userDataCollection, userData);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userData);
      });

      it('should add only unique UserData to an array', () => {
        const userDataArray: IUserData[] = [{ id: 123 }, { id: 456 }, { id: 15071 }];
        const userDataCollection: IUserData[] = [{ id: 123 }];
        expectedResult = service.addUserDataToCollectionIfMissing(userDataCollection, ...userDataArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userData: IUserData = { id: 123 };
        const userData2: IUserData = { id: 456 };
        expectedResult = service.addUserDataToCollectionIfMissing([], userData, userData2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userData);
        expect(expectedResult).toContain(userData2);
      });

      it('should accept null and undefined values', () => {
        const userData: IUserData = { id: 123 };
        expectedResult = service.addUserDataToCollectionIfMissing([], null, userData, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userData);
      });

      it('should return initial array if no UserData is added', () => {
        const userDataCollection: IUserData[] = [{ id: 123 }];
        expectedResult = service.addUserDataToCollectionIfMissing(userDataCollection, undefined, null);
        expect(expectedResult).toEqual(userDataCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
