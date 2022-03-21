import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISettingsList, SettingsList } from '../settings-list.model';

import { SettingsListService } from './settings-list.service';

describe('SettingsList Service', () => {
  let service: SettingsListService;
  let httpMock: HttpTestingController;
  let elemDefault: ISettingsList;
  let expectedResult: ISettingsList | ISettingsList[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SettingsListService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      settingOne: false,
      settingTwo: false,
      settingThree: false,
      settingFour: false,
      settingFive: false,
      settingSix: false,
      settingSeven: false,
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

    it('should create a SettingsList', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new SettingsList()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SettingsList', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          settingOne: true,
          settingTwo: true,
          settingThree: true,
          settingFour: true,
          settingFive: true,
          settingSix: true,
          settingSeven: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SettingsList', () => {
      const patchObject = Object.assign(
        {
          settingTwo: true,
          settingFour: true,
          settingFive: true,
          settingSix: true,
        },
        new SettingsList()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SettingsList', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          settingOne: true,
          settingTwo: true,
          settingThree: true,
          settingFour: true,
          settingFive: true,
          settingSix: true,
          settingSeven: true,
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

    it('should delete a SettingsList', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSettingsListToCollectionIfMissing', () => {
      it('should add a SettingsList to an empty array', () => {
        const settingsList: ISettingsList = { id: 123 };
        expectedResult = service.addSettingsListToCollectionIfMissing([], settingsList);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(settingsList);
      });

      it('should not add a SettingsList to an array that contains it', () => {
        const settingsList: ISettingsList = { id: 123 };
        const settingsListCollection: ISettingsList[] = [
          {
            ...settingsList,
          },
          { id: 456 },
        ];
        expectedResult = service.addSettingsListToCollectionIfMissing(settingsListCollection, settingsList);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SettingsList to an array that doesn't contain it", () => {
        const settingsList: ISettingsList = { id: 123 };
        const settingsListCollection: ISettingsList[] = [{ id: 456 }];
        expectedResult = service.addSettingsListToCollectionIfMissing(settingsListCollection, settingsList);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(settingsList);
      });

      it('should add only unique SettingsList to an array', () => {
        const settingsListArray: ISettingsList[] = [{ id: 123 }, { id: 456 }, { id: 68781 }];
        const settingsListCollection: ISettingsList[] = [{ id: 123 }];
        expectedResult = service.addSettingsListToCollectionIfMissing(settingsListCollection, ...settingsListArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const settingsList: ISettingsList = { id: 123 };
        const settingsList2: ISettingsList = { id: 456 };
        expectedResult = service.addSettingsListToCollectionIfMissing([], settingsList, settingsList2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(settingsList);
        expect(expectedResult).toContain(settingsList2);
      });

      it('should accept null and undefined values', () => {
        const settingsList: ISettingsList = { id: 123 };
        expectedResult = service.addSettingsListToCollectionIfMissing([], null, settingsList, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(settingsList);
      });

      it('should return initial array if no SettingsList is added', () => {
        const settingsListCollection: ISettingsList[] = [{ id: 123 }];
        expectedResult = service.addSettingsListToCollectionIfMissing(settingsListCollection, undefined, null);
        expect(expectedResult).toEqual(settingsListCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
