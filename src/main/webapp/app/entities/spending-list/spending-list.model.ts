import { IUserPending } from 'app/entities/user-pending/user-pending.model';

export interface ISpendingList {
  id?: number;
  total?: number | null;
  nameSpendList?: string;
  spendings?: IUserPending[] | null;
}

export class SpendingList implements ISpendingList {
  constructor(public id?: number, public total?: number | null, public nameSpendList?: string, public spendings?: IUserPending[] | null) {}
}

export function getSpendingListIdentifier(spendingList: ISpendingList): number | undefined {
  return spendingList.id;
}
