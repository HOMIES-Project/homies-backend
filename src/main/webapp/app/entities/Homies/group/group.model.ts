import dayjs from 'dayjs/esm';

export interface IGroup {
  id?: number;
  groupKey?: string | null;
  groupName?: string;
  groupRelationName?: string;
  addGroupDate?: dayjs.Dayjs | null;
}

export class Group implements IGroup {
  constructor(
    public id?: number,
    public groupKey?: string | null,
    public groupName?: string,
    public groupRelationName?: string,
    public addGroupDate?: dayjs.Dayjs | null
  ) {}
}

export function getGroupIdentifier(group: IGroup): number | undefined {
  return group.id;
}
