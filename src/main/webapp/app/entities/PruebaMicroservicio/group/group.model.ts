import dayjs from 'dayjs/esm';
import { IUserName } from 'app/entities/PruebaMicroservicio/user-name/user-name.model';

export interface IGroup {
  id?: number;
  groupKey?: string | null;
  groupName?: string;
  groupRelationName?: string;
  addGroupDate?: dayjs.Dayjs | null;
  idUserName?: number;
  userNames?: IUserName[] | null;
}

export class Group implements IGroup {
  constructor(
    public id?: number,
    public groupKey?: string | null,
    public groupName?: string,
    public groupRelationName?: string,
    public addGroupDate?: dayjs.Dayjs | null,
    public idUserName?: number,
    public userNames?: IUserName[] | null
  ) {}
}

export function getGroupIdentifier(group: IGroup): number | undefined {
  return group.id;
}
