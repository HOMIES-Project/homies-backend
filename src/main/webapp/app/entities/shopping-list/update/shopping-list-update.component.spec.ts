import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ShoppingListService } from '../service/shopping-list.service';
import { IShoppingList, ShoppingList } from '../shopping-list.model';
import { IGroup } from 'app/entities/Homies/group/group.model';
import { GroupService } from 'app/entities/Homies/group/service/group.service';

import { ShoppingListUpdateComponent } from './shopping-list-update.component';

describe('ShoppingList Management Update Component', () => {
  let comp: ShoppingListUpdateComponent;
  let fixture: ComponentFixture<ShoppingListUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let shoppingListService: ShoppingListService;
  let groupService: GroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ShoppingListUpdateComponent],
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
      .overrideTemplate(ShoppingListUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ShoppingListUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    shoppingListService = TestBed.inject(ShoppingListService);
    groupService = TestBed.inject(GroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call group query and add missing value', () => {
      const shoppingList: IShoppingList = { id: 456 };
      const group: IGroup = { id: 11409 };
      shoppingList.group = group;

      const groupCollection: IGroup[] = [{ id: 49041 }];
      jest.spyOn(groupService, 'query').mockReturnValue(of(new HttpResponse({ body: groupCollection })));
      const expectedCollection: IGroup[] = [group, ...groupCollection];
      jest.spyOn(groupService, 'addGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ shoppingList });
      comp.ngOnInit();

      expect(groupService.query).toHaveBeenCalled();
      expect(groupService.addGroupToCollectionIfMissing).toHaveBeenCalledWith(groupCollection, group);
      expect(comp.groupsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const shoppingList: IShoppingList = { id: 456 };
      const group: IGroup = { id: 14519 };
      shoppingList.group = group;

      activatedRoute.data = of({ shoppingList });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(shoppingList));
      expect(comp.groupsCollection).toContain(group);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ShoppingList>>();
      const shoppingList = { id: 123 };
      jest.spyOn(shoppingListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shoppingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shoppingList }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(shoppingListService.update).toHaveBeenCalledWith(shoppingList);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ShoppingList>>();
      const shoppingList = new ShoppingList();
      jest.spyOn(shoppingListService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shoppingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shoppingList }));
      saveSubject.complete();

      // THEN
      expect(shoppingListService.create).toHaveBeenCalledWith(shoppingList);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ShoppingList>>();
      const shoppingList = { id: 123 };
      jest.spyOn(shoppingListService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shoppingList });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(shoppingListService.update).toHaveBeenCalledWith(shoppingList);
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
