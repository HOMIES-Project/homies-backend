import dayjs from 'dayjs/esm';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { IUser } from 'app/entities/user/user.model';

export interface IUserData {
  id?: number;
  photoContentType?: string | null;
  photo?: string | null;
  phone?: string | null;
  premium?: boolean;
  birthDate?: dayjs.Dayjs | null;
  addDate?: dayjs.Dayjs | null;
  groups?: IGroup[] | null;
  user?: IUser | null;
  adminGroups?: IGroup[] | null;
}

export class UserData implements IUserData {
  constructor(
    public id?: number,
    public photoContentType?: string | null,
    public photo?: string | null,
    public phone?: string | null,
    public premium?: boolean,
    public birthDate?: dayjs.Dayjs | null,
    public addDate?: dayjs.Dayjs | null,
    public groups?: IGroup[] | null,
    public user?: IUser | null,
    public adminGroups?: IGroup[] | null
  ) {
    this.premium = this.premium ?? false;
  }
}

export function getUserDataIdentifier(userData: IUserData): number | undefined {
  return userData.id;
}
