import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISettingsList, getSettingsListIdentifier } from '../settings-list.model';

export type EntityResponseType = HttpResponse<ISettingsList>;
export type EntityArrayResponseType = HttpResponse<ISettingsList[]>;

@Injectable({ providedIn: 'root' })
export class SettingsListService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/settings-lists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(settingsList: ISettingsList): Observable<EntityResponseType> {
    return this.http.post<ISettingsList>(this.resourceUrl, settingsList, { observe: 'response' });
  }

  update(settingsList: ISettingsList): Observable<EntityResponseType> {
    return this.http.put<ISettingsList>(`${this.resourceUrl}/${getSettingsListIdentifier(settingsList) as number}`, settingsList, {
      observe: 'response',
    });
  }

  partialUpdate(settingsList: ISettingsList): Observable<EntityResponseType> {
    return this.http.patch<ISettingsList>(`${this.resourceUrl}/${getSettingsListIdentifier(settingsList) as number}`, settingsList, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISettingsList>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISettingsList[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSettingsListToCollectionIfMissing(
    settingsListCollection: ISettingsList[],
    ...settingsListsToCheck: (ISettingsList | null | undefined)[]
  ): ISettingsList[] {
    const settingsLists: ISettingsList[] = settingsListsToCheck.filter(isPresent);
    if (settingsLists.length > 0) {
      const settingsListCollectionIdentifiers = settingsListCollection.map(
        settingsListItem => getSettingsListIdentifier(settingsListItem)!
      );
      const settingsListsToAdd = settingsLists.filter(settingsListItem => {
        const settingsListIdentifier = getSettingsListIdentifier(settingsListItem);
        if (settingsListIdentifier == null || settingsListCollectionIdentifiers.includes(settingsListIdentifier)) {
          return false;
        }
        settingsListCollectionIdentifiers.push(settingsListIdentifier);
        return true;
      });
      return [...settingsListsToAdd, ...settingsListCollection];
    }
    return settingsListCollection;
  }
}
