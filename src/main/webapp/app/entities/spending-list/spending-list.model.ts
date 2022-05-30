import { IUserPending } from 'app/entities/user-pending/user-pending.model';
import { ISettingsList } from 'app/entities/settings-list/settings-list.model';
import { IGroup } from 'app/entities/Homies/group/group.model';

export interface ISpendingList {
  id?: number;
  total?: number | null;
  nameSpendList?: string;
  spendings?: IUserPending[] | null;
  settingsLists?: ISettingsList[] | null;
  group?: IGroup | null;
}

export class SpendingList implements ISpendingList {
  constructor(
    public id?: number,
    public total?: number | null,
    public nameSpendList?: string,
    public spendings?: IUserPending[] | null,
    public settingsLists?: ISettingsList[] | null,
    public group?: IGroup | null
  ) {}
}

export function getSpendingListIdentifier(spendingList: ISpendingList): number | undefined {
  return spendingList.id;
}
