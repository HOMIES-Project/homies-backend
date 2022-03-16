import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IUserName, UserName } from '../user-name.model';

import { UserNameService } from './user-name.service';

describe('UserName Service', () => {
  let service: UserNameService;
  let httpMock: HttpTestingController;
  let elemDefault: IUserName;
  let expectedResult: IUserName | IUserName[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserNameService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      email: 'AAAAAAA',
      nick: 'AAAAAAA',
      password: 'AAAAAAA',
      name: 'AAAAAAA',
      surname: 'AAAAAAA',
      photoContentType: 'image/png',
      photo: 'AAAAAAA',
      phone: 'AAAAAAA',
      premium: false,
      birthDate: currentDate,
      addDate: currentDate,
      token: 'AAAAAAA',
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

    it('should create a UserName', () => {
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

      service.create(new UserName()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserName', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          email: 'BBBBBB',
          nick: 'BBBBBB',
          password: 'BBBBBB',
          name: 'BBBBBB',
          surname: 'BBBBBB',
          photo: 'BBBBBB',
          phone: 'BBBBBB',
          premium: true,
          birthDate: currentDate.format(DATE_FORMAT),
          addDate: currentDate.format(DATE_FORMAT),
          token: 'BBBBBB',
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

    it('should partial update a UserName', () => {
      const patchObject = Object.assign(
        {
          email: 'BBBBBB',
          nick: 'BBBBBB',
          password: 'BBBBBB',
          addDate: currentDate.format(DATE_FORMAT),
        },
        new UserName()
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

    it('should return a list of UserName', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          email: 'BBBBBB',
          nick: 'BBBBBB',
          password: 'BBBBBB',
          name: 'BBBBBB',
          surname: 'BBBBBB',
          photo: 'BBBBBB',
          phone: 'BBBBBB',
          premium: true,
          birthDate: currentDate.format(DATE_FORMAT),
          addDate: currentDate.format(DATE_FORMAT),
          token: 'BBBBBB',
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

    it('should delete a UserName', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUserNameToCollectionIfMissing', () => {
      it('should add a UserName to an empty array', () => {
        const userName: IUserName = { id: 123 };
        expectedResult = service.addUserNameToCollectionIfMissing([], userName);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userName);
      });

      it('should not add a UserName to an array that contains it', () => {
        const userName: IUserName = { id: 123 };
        const userNameCollection: IUserName[] = [
          {
            ...userName,
          },
          { id: 456 },
        ];
        expectedResult = service.addUserNameToCollectionIfMissing(userNameCollection, userName);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserName to an array that doesn't contain it", () => {
        const userName: IUserName = { id: 123 };
        const userNameCollection: IUserName[] = [{ id: 456 }];
        expectedResult = service.addUserNameToCollectionIfMissing(userNameCollection, userName);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userName);
      });

      it('should add only unique UserName to an array', () => {
        const userNameArray: IUserName[] = [{ id: 123 }, { id: 456 }, { id: 23031 }];
        const userNameCollection: IUserName[] = [{ id: 123 }];
        expectedResult = service.addUserNameToCollectionIfMissing(userNameCollection, ...userNameArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userName: IUserName = { id: 123 };
        const userName2: IUserName = { id: 456 };
        expectedResult = service.addUserNameToCollectionIfMissing([], userName, userName2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userName);
        expect(expectedResult).toContain(userName2);
      });

      it('should accept null and undefined values', () => {
        const userName: IUserName = { id: 123 };
        expectedResult = service.addUserNameToCollectionIfMissing([], null, userName, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userName);
      });

      it('should return initial array if no UserName is added', () => {
        const userNameCollection: IUserName[] = [{ id: 123 }];
        expectedResult = service.addUserNameToCollectionIfMissing(userNameCollection, undefined, null);
        expect(expectedResult).toEqual(userNameCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
