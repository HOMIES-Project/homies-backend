import dayjs from 'dayjs/esm';
import { IGroup } from 'app/entities/PruebaMicroservicio/group/group.model';

export interface IUserName {
  id?: number;
  email?: string;
  nick?: string;
  password?: string;
  name?: string;
  surname?: string;
  photoContentType?: string | null;
  photo?: string | null;
  phone?: string | null;
  premium?: boolean;
  birthDate?: dayjs.Dayjs | null;
  addDate?: dayjs.Dayjs | null;
  token?: string | null;
  groups?: IGroup[] | null;
}

export class UserName implements IUserName {
  constructor(
    public id?: number,
    public email?: string,
    public nick?: string,
    public password?: string,
    public name?: string,
    public surname?: string,
    public photoContentType?: string | null,
    public photo?: string | null,
    public phone?: string | null,
    public premium?: boolean,
    public birthDate?: dayjs.Dayjs | null,
    public addDate?: dayjs.Dayjs | null,
    public token?: string | null,
    public groups?: IGroup[] | null
  ) {
    this.premium = this.premium ?? false;
  }
}

export function getUserNameIdentifier(userName: IUserName): number | undefined {
  return userName.id;
}
