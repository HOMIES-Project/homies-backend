import { ISpendingList } from 'app/entities/spending-list/spending-list.model';

export interface IUserPending {
  id?: number;
  pending?: number | null;
  paid?: boolean | null;
  spendingList?: ISpendingList | null;
}

export class UserPending implements IUserPending {
  constructor(
    public id?: number,
    public pending?: number | null,
    public paid?: boolean | null,
    public spendingList?: ISpendingList | null
  ) {
    this.paid = this.paid ?? false;
  }
}

export function getUserPendingIdentifier(userPending: IUserPending): number | undefined {
  return userPending.id;
}
