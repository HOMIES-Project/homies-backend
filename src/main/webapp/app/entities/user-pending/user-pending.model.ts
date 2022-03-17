export interface IUserPending {
  id?: number;
  pending?: number | null;
  paid?: boolean | null;
}

export class UserPending implements IUserPending {
  constructor(public id?: number, public pending?: number | null, public paid?: boolean | null) {
    this.paid = this.paid ?? false;
  }
}

export function getUserPendingIdentifier(userPending: IUserPending): number | undefined {
  return userPending.id;
}
