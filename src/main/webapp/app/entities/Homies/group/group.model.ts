import dayjs from 'dayjs/esm';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { ITaskList } from 'app/entities/task-list/task-list.model';
import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { IShoppingList } from 'app/entities/shopping-list/shopping-list.model';
import { ISettingsList } from 'app/entities/settings-list/settings-list.model';

export interface IGroup {
  id?: number;
  groupKey?: string | null;
  groupName?: string;
  groupRelationName?: string;
  addGroupDate?: dayjs.Dayjs | null;
  userAdmin?: IUserData | null;
  taskList?: ITaskList | null;
  spendingList?: ISpendingList | null;
  shoppingList?: IShoppingList | null;
  settingsList?: ISettingsList | null;
  userData?: IUserData[] | null;
}

export class Group implements IGroup {
  constructor(
    public id?: number,
    public groupKey?: string | null,
    public groupName?: string,
    public groupRelationName?: string,
    public addGroupDate?: dayjs.Dayjs | null,
    public userAdmin?: IUserData | null,
    public taskList?: ITaskList | null,
    public spendingList?: ISpendingList | null,
    public shoppingList?: IShoppingList | null,
    public settingsList?: ISettingsList | null,
    public userData?: IUserData[] | null
  ) {}
}

export function getGroupIdentifier(group: IGroup): number | undefined {
  return group.id;
}
