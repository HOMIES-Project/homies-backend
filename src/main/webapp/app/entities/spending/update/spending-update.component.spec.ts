import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpendingService } from '../service/spending.service';
import { ISpending, Spending } from '../spending.model';

import { SpendingUpdateComponent } from './spending-update.component';

describe('Spending Management Update Component', () => {
  let comp: SpendingUpdateComponent;
  let fixture: ComponentFixture<SpendingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let spendingService: SpendingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SpendingUpdateComponent],
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
      .overrideTemplate(SpendingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpendingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    spendingService = TestBed.inject(SpendingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const spending: ISpending = { id: 456 };

      activatedRoute.data = of({ spending });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(spending));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Spending>>();
      const spending = { id: 123 };
      jest.spyOn(spendingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spending });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spending }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(spendingService.update).toHaveBeenCalledWith(spending);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Spending>>();
      const spending = new Spending();
      jest.spyOn(spendingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spending });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spending }));
      saveSubject.complete();

      // THEN
      expect(spendingService.create).toHaveBeenCalledWith(spending);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Spending>>();
      const spending = { id: 123 };
      jest.spyOn(spendingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spending });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(spendingService.update).toHaveBeenCalledWith(spending);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
