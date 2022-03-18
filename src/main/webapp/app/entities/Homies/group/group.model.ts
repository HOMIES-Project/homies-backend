import dayjs from 'dayjs/esm';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { ITaskList } from 'app/entities/task-list/task-list.model';

export interface IGroup {
  id?: number;
  groupKey?: string | null;
  groupName?: string;
  groupRelationName?: string;
  addGroupDate?: dayjs.Dayjs | null;
  userData?: IUserData[] | null;
  userAdmin?: IUserData | null;
  taskList?: ITaskList | null;
}

export class Group implements IGroup {
  constructor(
    public id?: number,
    public groupKey?: string | null,
    public groupName?: string,
    public groupRelationName?: string,
    public addGroupDate?: dayjs.Dayjs | null,
    public userData?: IUserData[] | null,
    public userAdmin?: IUserData | null,
    public taskList?: ITaskList | null
  ) {}
}

export function getGroupIdentifier(group: IGroup): number | undefined {
  return group.id;
}
