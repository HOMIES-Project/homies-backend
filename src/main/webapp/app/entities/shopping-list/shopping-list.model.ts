export interface IShoppingList {
  id?: number;
  name?: string;
  total?: number | null;
}

export class ShoppingList implements IShoppingList {
  constructor(public id?: number, public name?: string, public total?: number | null) {}
}

export function getShoppingListIdentifier(shoppingList: IShoppingList): number | undefined {
  return shoppingList.id;
}
