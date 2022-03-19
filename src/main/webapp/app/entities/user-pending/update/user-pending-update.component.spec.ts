import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserPendingService } from '../service/user-pending.service';
import { IUserPending, UserPending } from '../user-pending.model';
import { ISpendingList } from 'app/entities/spending-list/spending-list.model';
import { SpendingListService } from 'app/entities/spending-list/service/spending-list.service';

import { UserPendingUpdateComponent } from './user-pending-update.component';

describe('UserPending Management Update Component', () => {
  let comp: UserPendingUpdateComponent;
  let fixture: ComponentFixture<UserPendingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userPendingService: UserPendingService;
  let spendingListService: SpendingListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserPendingUpdateComponent],
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
      .overrideTemplate(UserPendingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserPendingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userPendingService = TestBed.inject(UserPendingService);
    spendingListService = TestBed.inject(SpendingListService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call SpendingList query and add missing value', () => {
      const userPending: IUserPending = { id: 456 };
      const spendingList: ISpendingList = { id: 31633 };
      userPending.spendingList = spendingList;

      const spendingListCollection: ISpendingList[] = [{ id: 31508 }];
      jest.spyOn(spendingListService, 'query').mockReturnValue(of(new HttpResponse({ body: spendingListCollection })));
      const additionalSpendingLists = [spendingList];
      const expectedCollection: ISpendingList[] = [...additionalSpendingLists, ...spendingListCollection];
      jest.spyOn(spendingListService, 'addSpendingListToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userPending });
      comp.ngOnInit();

      expect(spendingListService.query).toHaveBeenCalled();
      expect(spendingListService.addSpendingListToCollectionIfMissing).toHaveBeenCalledWith(
        spendingListCollection,
        ...additionalSpendingLists
      );
      expect(comp.spendingListsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userPending: IUserPending = { id: 456 };
      const spendingList: ISpendingList = { id: 74574 };
      userPending.spendingList = spendingList;

      activatedRoute.data = of({ userPending });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(userPending));
      expect(comp.spendingListsSharedCollection).toContain(spendingList);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserPending>>();
      const userPending = { id: 123 };
      jest.spyOn(userPendingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPending });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userPending }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(userPendingService.update).toHaveBeenCalledWith(userPending);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserPending>>();
      const userPending = new UserPending();
      jest.spyOn(userPendingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPending });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userPending }));
      saveSubject.complete();

      // THEN
      expect(userPendingService.create).toHaveBeenCalledWith(userPending);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<UserPending>>();
      const userPending = { id: 123 };
      jest.spyOn(userPendingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPending });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userPendingService.update).toHaveBeenCalledWith(userPending);
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
  });
});
