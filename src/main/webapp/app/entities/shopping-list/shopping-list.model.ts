import { IProducts } from 'app/entities/products/products.model';
import { IGroup } from 'app/entities/Homies/group/group.model';

export interface IShoppingList {
  id?: number;
  total?: number | null;
  nameShopList?: string;
  products?: IProducts[] | null;
  group?: IGroup | null;
}

export class ShoppingList implements IShoppingList {
  constructor(
    public id?: number,
    public total?: number | null,
    public nameShopList?: string,
    public products?: IProducts[] | null,
    public group?: IGroup | null
  ) {}
}

export function getShoppingListIdentifier(shoppingList: IShoppingList): number | undefined {
  return shoppingList.id;
}
