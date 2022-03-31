import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpendingListService } from '../service/spending-list.service';
import { ISpendingList, SpendingList } from '../spending-list.model';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';

import { SpendingListUpdateComponent } from './spending-list-update.component';

describe('SpendingList Management Update Component', () => {
  let comp: SpendingListUpdateComponent;
  let fixture: ComponentFixture<SpendingListUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let spendingListService: SpendingListService;
  let groupService: GroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SpendingListUpdateComponent],
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
      .overrideTemplate(SpendingListUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpendingListUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    spendingListService = TestBed.inject(SpendingListService);
    groupService = TestBed.inject(GroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call group query and add missing value', () => {
      const spendingList: ISpendingList = { id: 456 };
      const group: IGroup = { id: 2214 };
      spendingList.group = group;

      const groupCollection: IGroup[] = [{ id: 65514 }];
      jest.spyOn(groupService, 'query').mockReturnValue(of(new HttpResponse({ body: groupCollection })));
      const expectedCollection: IGroup[] = [group, ...groupCollection];
      jest.spyOn(groupService, 'addGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ spendingList });
      comp.ngOnInit();

      expect(groupService.query).toHaveBeenCalled();
      expect(groupService.addGroupToCollectionIfMissing).toHaveBeenCalledWith(groupCollection, group);
      expect(comp.groupsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const spendingList: ISpendingList = { id: 456 };
      const group: IGroup = { id: 55997 };
      spendingList.group = group;

      activatedRoute.data = of({ spendingList });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(spendingList));
      expect(comp.groupsCollection).toContain(group);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SpendingList>>();
      const spendingList = { id: 123 };
      jest.spyOn(spendingListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spendingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spendingList }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(spendingListService.update).toHaveBeenCalledWith(spendingList);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SpendingList>>();
      const spendingList = new SpendingList();
      jest.spyOn(spendingListService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spendingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spendingList }));
      saveSubject.complete();

      // THEN
      expect(spendingListService.create).toHaveBeenCalledWith(spendingList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SpendingList>>();
      const spendingList = { id: 123 };
      jest.spyOn(spendingListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spendingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(spendingListService.update).toHaveBeenCalledWith(spendingList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackGroupById', () => {
      it('Should return tracked Group primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGroupById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
