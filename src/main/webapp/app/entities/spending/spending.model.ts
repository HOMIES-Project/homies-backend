import { IUserPending } from 'app/entities/user-pending/user-pending.model';

export interface ISpending {
  id?: number;
  payer?: number;
  nameCost?: string;
  cost?: number;
  photoContentType?: string | null;
  photo?: string | null;
  descripcion?: string | null;
  paid?: number | null;
  pending?: number | null;
  finished?: boolean | null;
  userPendings?: IUserPending[] | null;
}

export class Spending implements ISpending {
  constructor(
    public id?: number,
    public payer?: number,
    public nameCost?: string,
    public cost?: number,
    public photoContentType?: string | null,
    public photo?: string | null,
    public descripcion?: string | null,
    public paid?: number | null,
    public pending?: number | null,
    public finished?: boolean | null,
    public userPendings?: IUserPending[] | null
  ) {
    this.finished = this.finished ?? false;
  }
}

export function getSpendingIdentifier(spending: ISpending): number | undefined {
  return spending.id;
}
