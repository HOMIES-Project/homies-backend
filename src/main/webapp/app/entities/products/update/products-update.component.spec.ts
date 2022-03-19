import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductsService } from '../service/products.service';
import { IProducts, Products } from '../products.model';
import { IUserData } from 'app/entities/Homies/user-data/user-data.model';
import { UserDataService } from 'app/entities/Homies/user-data/service/user-data.service';
import { IShoppingList } from 'app/entities/shopping-list/shopping-list.model';
import { ShoppingListService } from 'app/entities/shopping-list/service/shopping-list.service';

import { ProductsUpdateComponent } from './products-update.component';

describe('Products Management Update Component', () => {
  let comp: ProductsUpdateComponent;
  let fixture: ComponentFixture<ProductsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productsService: ProductsService;
  let userDataService: UserDataService;
  let shoppingListService: ShoppingListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductsUpdateComponent],
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
      .overrideTemplate(ProductsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productsService = TestBed.inject(ProductsService);
    userDataService = TestBed.inject(UserDataService);
    shoppingListService = TestBed.inject(ShoppingListService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserData query and add missing value', () => {
      const products: IProducts = { id: 456 };
      const userCreator: IUserData = { id: 61230 };
      products.userCreator = userCreator;

      const userDataCollection: IUserData[] = [{ id: 67020 }];
      jest.spyOn(userDataService, 'query').mockReturnValue(of(new HttpResponse({ body: userDataCollection })));
      const additionalUserData = [userCreator];
      const expectedCollection: IUserData[] = [...additionalUserData, ...userDataCollection];
      jest.spyOn(userDataService, 'addUserDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(userDataService.query).toHaveBeenCalled();
      expect(userDataService.addUserDataToCollectionIfMissing).toHaveBeenCalledWith(userDataCollection, ...additionalUserData);
      expect(comp.userDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ShoppingList query and add missing value', () => {
      const products: IProducts = { id: 456 };
      const shoppingList: IShoppingList = { id: 42350 };
      products.shoppingList = shoppingList;

      const shoppingListCollection: IShoppingList[] = [{ id: 25415 }];
      jest.spyOn(shoppingListService, 'query').mockReturnValue(of(new HttpResponse({ body: shoppingListCollection })));
      const additionalShoppingLists = [shoppingList];
      const expectedCollection: IShoppingList[] = [...additionalShoppingLists, ...shoppingListCollection];
      jest.spyOn(shoppingListService, 'addShoppingListToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(shoppingListService.query).toHaveBeenCalled();
      expect(shoppingListService.addShoppingListToCollectionIfMissing).toHaveBeenCalledWith(
        shoppingListCollection,
        ...additionalShoppingLists
      );
      expect(comp.shoppingListsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const products: IProducts = { id: 456 };
      const userCreator: IUserData = { id: 25638 };
      products.userCreator = userCreator;
      const shoppingList: IShoppingList = { id: 55839 };
      products.shoppingList = shoppingList;

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(products));
      expect(comp.userDataSharedCollection).toContain(userCreator);
      expect(comp.shoppingListsSharedCollection).toContain(shoppingList);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Products>>();
      const products = { id: 123 };
      jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: products }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(productsService.update).toHaveBeenCalledWith(products);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Products>>();
      const products = new Products();
      jest.spyOn(productsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: products }));
      saveSubject.complete();

      // THEN
      expect(productsService.create).toHaveBeenCalledWith(products);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Products>>();
      const products = { id: 123 };
      jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productsService.update).toHaveBeenCalledWith(products);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserDataById', () => {
      it('Should return tracked UserData primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserDataById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackShoppingListById', () => {
      it('Should return tracked ShoppingList primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackShoppingListById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
