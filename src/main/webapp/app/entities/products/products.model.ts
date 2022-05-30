import dayjs from 'dayjs/esm';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { IShoppingList } from 'app/entities/shopping-list/shopping-list.model';

export interface IProducts {
  id?: number;
  name?: string;
  price?: number | null;
  photoContentType?: string | null;
  photo?: string | null;
  units?: number;
  typeUnit?: string | null;
  note?: string | null;
  dataCreated?: dayjs.Dayjs | null;
  shoppingDate?: dayjs.Dayjs | null;
  purchased?: boolean | null;
  userCreated?: number | null;
  userCreator?: IUserData | null;
  shoppingList?: IShoppingList | null;
}

export class Products implements IProducts {
  constructor(
    public id?: number,
    public name?: string,
    public price?: number | null,
    public photoContentType?: string | null,
    public photo?: string | null,
    public units?: number,
    public typeUnit?: string | null,
    public note?: string | null,
    public dataCreated?: dayjs.Dayjs | null,
    public shoppingDate?: dayjs.Dayjs | null,
    public purchased?: boolean | null,
    public userCreated?: number | null,
    public userCreator?: IUserData | null,
    public shoppingList?: IShoppingList | null
  ) {
    this.purchased = this.purchased ?? false;
  }
}

export function getProductsIdentifier(products: IProducts): number | undefined {
  return products.id;
}
