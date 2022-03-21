import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISettingsList } from '../settings-list.model';

@Component({
  selector: 'jhi-settings-list-detail',
  templateUrl: './settings-list-detail.component.html',
})
export class SettingsListDetailComponent implements OnInit {
  settingsList: ISettingsList | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ settingsList }) => {
      this.settingsList = settingsList;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
