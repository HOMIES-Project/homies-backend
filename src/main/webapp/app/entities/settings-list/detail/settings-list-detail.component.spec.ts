import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SettingsListDetailComponent } from './settings-list-detail.component';

describe('SettingsList Management Detail Component', () => {
  let comp: SettingsListDetailComponent;
  let fixture: ComponentFixture<SettingsListDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SettingsListDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ settingsList: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SettingsListDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SettingsListDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load settingsList on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.settingsList).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
