import dayjs from 'dayjs/esm';

export interface IUserName {
  id?: number;
  name?: string;
  surname?: string;
  photoContentType?: string | null;
  photo?: string | null;
  phone?: string | null;
  premium?: boolean;
  birthDate?: dayjs.Dayjs | null;
  addDate?: dayjs.Dayjs | null;
}

export class UserName implements IUserName {
  constructor(
    public id?: number,
    public name?: string,
    public surname?: string,
    public photoContentType?: string | null,
    public photo?: string | null,
    public phone?: string | null,
    public premium?: boolean,
    public birthDate?: dayjs.Dayjs | null,
    public addDate?: dayjs.Dayjs | null
  ) {
    this.premium = this.premium ?? false;
  }
}

export function getUserNameIdentifier(userName: IUserName): number | undefined {
  return userName.id;
}
