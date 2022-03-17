export interface ISpendingList {
  id?: number;
  name?: string;
  total?: number | null;
}

export class SpendingList implements ISpendingList {
  constructor(public id?: number, public name?: string, public total?: number | null) {}
}

export function getSpendingListIdentifier(spendingList: ISpendingList): number | undefined {
  return spendingList.id;
}
