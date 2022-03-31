import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SettingsListService } from '../service/settings-list.service';
import { ISettingsList, SettingsList } from '../settings-list.model';
import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { SpendingListService } from 'app/entities/spending-list/service/spending-list.service';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';

import { SettingsListUpdateComponent } from './settings-list-update.component';

describe('SettingsList Management Update Component', () => {
  let comp: SettingsListUpdateComponent;
  let fixture: ComponentFixture<SettingsListUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let settingsListService: SettingsListService;
  let spendingListService: SpendingListService;
  let groupService: GroupService;

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
    spendingListService = TestBed.inject(SpendingListService);
    groupService = TestBed.inject(GroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call SpendingList query and add missing value', () => {
      const settingsList: ISettingsList = { id: 456 };
      const spendingList: ISpendingList = { id: 63703 };
      settingsList.spendingList = spendingList;

      const spendingListCollection: ISpendingList[] = [{ id: 43017 }];
      jest.spyOn(spendingListService, 'query').mockReturnValue(of(new HttpResponse({ body: spendingListCollection })));
      const additionalSpendingLists = [spendingList];
      const expectedCollection: ISpendingList[] = [...additionalSpendingLists, ...spendingListCollection];
      jest.spyOn(spendingListService, 'addSpendingListToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      expect(spendingListService.query).toHaveBeenCalled();
      expect(spendingListService.addSpendingListToCollectionIfMissing).toHaveBeenCalledWith(
        spendingListCollection,
        ...additionalSpendingLists
      );
      expect(comp.spendingListsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call group query and add missing value', () => {
      const settingsList: ISettingsList = { id: 456 };
      const group: IGroup = { id: 59090 };
      settingsList.group = group;

      const groupCollection: IGroup[] = [{ id: 73621 }];
      jest.spyOn(groupService, 'query').mockReturnValue(of(new HttpResponse({ body: groupCollection })));
      const expectedCollection: IGroup[] = [group, ...groupCollection];
      jest.spyOn(groupService, 'addGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      expect(groupService.query).toHaveBeenCalled();
      expect(groupService.addGroupToCollectionIfMissing).toHaveBeenCalledWith(groupCollection, group);
      expect(comp.groupsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const settingsList: ISettingsList = { id: 456 };
      const spendingList: ISpendingList = { id: 44925 };
      settingsList.spendingList = spendingList;
      const group: IGroup = { id: 53491 };
      settingsList.group = group;

      activatedRoute.data = of({ settingsList });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(settingsList));
      expect(comp.spendingListsSharedCollection).toContain(spendingList);
      expect(comp.groupsCollection).toContain(group);
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

  describe('Tracking relationships identifiers', () => {
    describe('trackSpendingListById', () => {
      it('Should return tracked SpendingList primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSpendingListById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackGroupById', () => {
      it('Should return tracked Group primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGroupById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
