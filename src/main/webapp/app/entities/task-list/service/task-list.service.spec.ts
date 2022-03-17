import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITaskList, TaskList } from '../task-list.model';

import { TaskListService } from './task-list.service';

describe('TaskList Service', () => {
  let service: TaskListService;
  let httpMock: HttpTestingController;
  let elemDefault: ITaskList;
  let expectedResult: ITaskList | ITaskList[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TaskListService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nameList: 'AAAAAAA',
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

    it('should create a TaskList', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TaskList()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaskList', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nameList: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaskList', () => {
      const patchObject = Object.assign(
        {
          nameList: 'BBBBBB',
        },
        new TaskList()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaskList', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nameList: 'BBBBBB',
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

    it('should delete a TaskList', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTaskListToCollectionIfMissing', () => {
      it('should add a TaskList to an empty array', () => {
        const taskList: ITaskList = { id: 123 };
        expectedResult = service.addTaskListToCollectionIfMissing([], taskList);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskList);
      });

      it('should not add a TaskList to an array that contains it', () => {
        const taskList: ITaskList = { id: 123 };
        const taskListCollection: ITaskList[] = [
          {
            ...taskList,
          },
          { id: 456 },
        ];
        expectedResult = service.addTaskListToCollectionIfMissing(taskListCollection, taskList);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaskList to an array that doesn't contain it", () => {
        const taskList: ITaskList = { id: 123 };
        const taskListCollection: ITaskList[] = [{ id: 456 }];
        expectedResult = service.addTaskListToCollectionIfMissing(taskListCollection, taskList);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskList);
      });

      it('should add only unique TaskList to an array', () => {
        const taskListArray: ITaskList[] = [{ id: 123 }, { id: 456 }, { id: 20278 }];
        const taskListCollection: ITaskList[] = [{ id: 123 }];
        expectedResult = service.addTaskListToCollectionIfMissing(taskListCollection, ...taskListArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taskList: ITaskList = { id: 123 };
        const taskList2: ITaskList = { id: 456 };
        expectedResult = service.addTaskListToCollectionIfMissing([], taskList, taskList2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskList);
        expect(expectedResult).toContain(taskList2);
      });

      it('should accept null and undefined values', () => {
        const taskList: ITaskList = { id: 123 };
        expectedResult = service.addTaskListToCollectionIfMissing([], null, taskList, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskList);
      });

      it('should return initial array if no TaskList is added', () => {
        const taskListCollection: ITaskList[] = [{ id: 123 }];
        expectedResult = service.addTaskListToCollectionIfMissing(taskListCollection, undefined, null);
        expect(expectedResult).toEqual(taskListCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
