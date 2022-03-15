import dayjs from 'dayjs/esm';
import { IUserName } from 'app/entities/PruebaMicroservicio/user-name/user-name.model';

export interface IUserData {
  id?: number;
  name?: string;
  surname?: string;
  photoContentType?: string | null;
  photo?: string | null;
  email?: string;
  phone?: string | null;
  premium?: boolean;
  birthDate?: dayjs.Dayjs | null;
  addDate?: dayjs.Dayjs | null;
  userName?: IUserName | null;
}

export class UserData implements IUserData {
  constructor(
    public id?: number,
    public name?: string,
    public surname?: string,
    public photoContentType?: string | null,
    public photo?: string | null,
    public email?: string,
    public phone?: string | null,
    public premium?: boolean,
    public birthDate?: dayjs.Dayjs | null,
    public addDate?: dayjs.Dayjs | null,
    public userName?: IUserName | null
  ) {
    this.premium = this.premium ?? false;
  }
}

export function getUserDataIdentifier(userData: IUserData): number | undefined {
  return userData.id;
}
