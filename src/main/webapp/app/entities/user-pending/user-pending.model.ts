import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { ISpending } from 'app/entities/spending/spending.model';

export interface IUserPending {
  id?: number;
  pending?: number | null;
  paid?: boolean | null;
  spendingList?: ISpendingList | null;
  spendings?: ISpending[] | null;
}

export class UserPending implements IUserPending {
  constructor(
    public id?: number,
    public pending?: number | null,
    public paid?: boolean | null,
    public spendingList?: ISpendingList | null,
    public spendings?: ISpending[] | null
  ) {
    this.paid = this.paid ?? false;
  }
}

export function getUserPendingIdentifier(userPending: IUserPending): number | undefined {
  return userPending.id;
}
