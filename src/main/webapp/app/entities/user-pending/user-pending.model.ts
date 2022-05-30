import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { ISpending } from 'app/entities/spending/spending.model';
import { ISettingsList } from 'app/entities/settings-list/settings-list.model';

export interface IUserPending {
  id?: number;
  pending?: number | null;
  paid?: boolean | null;
  spendingList?: ISpendingList | null;
  spendings?: ISpending[] | null;
  settingsList?: ISettingsList | null;
}

export class UserPending implements IUserPending {
  constructor(
    public id?: number,
    public pending?: number | null,
    public paid?: boolean | null,
    public spendingList?: ISpendingList | null,
    public spendings?: ISpending[] | null,
    public settingsList?: ISettingsList | null
  ) {
    this.paid = this.paid ?? false;
  }
}

export function getUserPendingIdentifier(userPending: IUserPending): number | undefined {
  return userPending.id;
}
