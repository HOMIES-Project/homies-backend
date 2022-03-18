export interface ISpendingList {
  id?: number;
  total?: number | null;
  nameSpendList?: string;
}

export class SpendingList implements ISpendingList {
  constructor(public id?: number, public total?: number | null, public nameSpendList?: string) {}
}

export function getSpendingListIdentifier(spendingList: ISpendingList): number | undefined {
  return spendingList.id;
}
