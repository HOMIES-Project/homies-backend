import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SettingsListService } from '../service/settings-list.service';
import { ISettingsList, SettingsList } from '../settings-list.model';

import { SettingsListUpdateComponent } from './settings-list-update.component';

describe('SettingsList Management Update Component', () => {
  let comp: SettingsListUpdateComponent;
  let fixture: ComponentFixture<SettingsListUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let settingsListService: SettingsListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SettingsListUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SettingsListUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SettingsListUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    settingsListService = TestBed.inject(SettingsListService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const settingsList: ISettingsList = { id: 456 };

      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(settingsList));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SettingsList>>();
      const settingsList = { id: 123 };
      jest.spyOn(settingsListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: settingsList }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(settingsListService.update).toHaveBeenCalledWith(settingsList);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SettingsList>>();
      const settingsList = new SettingsList();
      jest.spyOn(settingsListService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: settingsList }));
      saveSubject.complete();

      // THEN
      expect(settingsListService.create).toHaveBeenCalledWith(settingsList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SettingsList>>();
      const settingsList = { id: 123 };
      jest.spyOn(settingsListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(settingsListService.update).toHaveBeenCalledWith(settingsList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
