import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskList, getTaskListIdentifier } from '../task-list.model';

export type EntityResponseType = HttpResponse<ITaskList>;
export type EntityArrayResponseType = HttpResponse<ITaskList[]>;

@Injectable({ providedIn: 'root' })
export class TaskListService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/task-lists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(taskList: ITaskList): Observable<EntityResponseType> {
    return this.http.post<ITaskList>(this.resourceUrl, taskList, { observe: 'response' });
  }

  update(taskList: ITaskList): Observable<EntityResponseType> {
    return this.http.put<ITaskList>(`${this.resourceUrl}/${getTaskListIdentifier(taskList) as number}`, taskList, { observe: 'response' });
  }

  partialUpdate(taskList: ITaskList): Observable<EntityResponseType> {
    return this.http.patch<ITaskList>(`${this.resourceUrl}/${getTaskListIdentifier(taskList) as number}`, taskList, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITaskList>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaskList[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTaskListToCollectionIfMissing(taskListCollection: ITaskList[], ...taskListsToCheck: (ITaskList | null | undefined)[]): ITaskList[] {
    const taskLists: ITaskList[] = taskListsToCheck.filter(isPresent);
    if (taskLists.length > 0) {
      const taskListCollectionIdentifiers = taskListCollection.map(taskListItem => getTaskListIdentifier(taskListItem)!);
      const taskListsToAdd = taskLists.filter(taskListItem => {
        const taskListIdentifier = getTaskListIdentifier(taskListItem);
        if (taskListIdentifier == null || taskListCollectionIdentifiers.includes(taskListIdentifier)) {
          return false;
        }
        taskListCollectionIdentifiers.push(taskListIdentifier);
        return true;
      });
      return [...taskListsToAdd, ...taskListCollection];
    }
    return taskListCollection;
  }
}
