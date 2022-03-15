import { IUserData } from 'app/entities/PruebaMicroservicio/user-data/user-data.model';
import { IGroup } from 'app/entities/PruebaMicroservicio/group/group.model';

export interface IUserName {
  id?: number;
  user_name?: string;
  password?: string;
  token?: string | null;
  userData?: IUserData;
  groups?: IGroup[] | null;
}

export class UserName implements IUserName {
  constructor(
    public id?: number,
    public user_name?: string,
    public password?: string,
    public token?: string | null,
    public userData?: IUserData,
    public groups?: IGroup[] | null
  ) {}
}

export function getUserNameIdentifier(userName: IUserName): number | undefined {
  return userName.id;
}
