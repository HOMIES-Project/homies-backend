export interface IShoppingList {
  id?: number;
  total?: number | null;
  nameShopList?: string;
}

export class ShoppingList implements IShoppingList {
  constructor(public id?: number, public total?: number | null, public nameShopList?: string) {}
}

export function getShoppingListIdentifier(shoppingList: IShoppingList): number | undefined {
  return shoppingList.id;
}
