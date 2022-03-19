import { ISpendingList } from 'app/entities/spending-list/spending-list.model';

export interface ISettingsList {
  id?: number;
  settingOne?: boolean | null;
  settingTwo?: boolean | null;
  settingThree?: boolean | null;
  settingFour?: boolean | null;
  settingFive?: boolean | null;
  settingSix?: boolean | null;
  settingSeven?: boolean | null;
  spendingList?: ISpendingList | null;
}

export class SettingsList implements ISettingsList {
  constructor(
    public id?: number,
    public settingOne?: boolean | null,
    public settingTwo?: boolean | null,
    public settingThree?: boolean | null,
    public settingFour?: boolean | null,
    public settingFive?: boolean | null,
    public settingSix?: boolean | null,
    public settingSeven?: boolean | null,
    public spendingList?: ISpendingList | null
  ) {
    this.settingOne = this.settingOne ?? false;
    this.settingTwo = this.settingTwo ?? false;
    this.settingThree = this.settingThree ?? false;
    this.settingFour = this.settingFour ?? false;
    this.settingFive = this.settingFive ?? false;
    this.settingSix = this.settingSix ?? false;
    this.settingSeven = this.settingSeven ?? false;
  }
}

export function getSettingsListIdentifier(settingsList: ISettingsList): number | undefined {
  return settingsList.id;
}
