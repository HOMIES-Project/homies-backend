package com.homies.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.homies.app.domain.Spending} entity. This class is used
 * in {@link com.homies.app.web.rest.SpendingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /spendings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SpendingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter payer;

    private StringFilter nameCost;

    private FloatFilter cost;

    private StringFilter descripcion;

    private FloatFilter paid;

    private FloatFilter pending;

    private BooleanFilter finished;

    private Boolean distinct;

    public SpendingCriteria() {}

    public SpendingCriteria(SpendingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.payer = other.payer == null ? null : other.payer.copy();
        this.nameCost = other.nameCost == null ? null : other.nameCost.copy();
        this.cost = other.cost == null ? null : other.cost.copy();
        this.descripcion = other.descripcion == null ? null : other.descripcion.copy();
        this.paid = other.paid == null ? null : other.paid.copy();
        this.pending = other.pending == null ? null : other.pending.copy();
        this.finished = other.finished == null ? null : other.finished.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SpendingCriteria copy() {
        return new SpendingCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getPayer() {
        return payer;
    }

    public IntegerFilter payer() {
        if (payer == null) {
            payer = new IntegerFilter();
        }
        return payer;
    }

    public void setPayer(IntegerFilter payer) {
        this.payer = payer;
    }

    public StringFilter getNameCost() {
        return nameCost;
    }

    public StringFilter nameCost() {
        if (nameCost == null) {
            nameCost = new StringFilter();
        }
        return nameCost;
    }

    public void setNameCost(StringFilter nameCost) {
        this.nameCost = nameCost;
    }

    public FloatFilter getCost() {
        return cost;
    }

    public FloatFilter cost() {
        if (cost == null) {
            cost = new FloatFilter();
        }
        return cost;
    }

    public void setCost(FloatFilter cost) {
        this.cost = cost;
    }

    public StringFilter getDescripcion() {
        return descripcion;
    }

    public StringFilter descripcion() {
        if (descripcion == null) {
            descripcion = new StringFilter();
        }
        return descripcion;
    }

    public void setDescripcion(StringFilter descripcion) {
        this.descripcion = descripcion;
    }

    public FloatFilter getPaid() {
        return paid;
    }

    public FloatFilter paid() {
        if (paid == null) {
            paid = new FloatFilter();
        }
        return paid;
    }

    public void setPaid(FloatFilter paid) {
        this.paid = paid;
    }

    public FloatFilter getPending() {
        return pending;
    }

    public FloatFilter pending() {
        if (pending == null) {
            pending = new FloatFilter();
        }
        return pending;
    }

    public void setPending(FloatFilter pending) {
        this.pending = pending;
    }

    public BooleanFilter getFinished() {
        return finished;
    }

    public BooleanFilter finished() {
        if (finished == null) {
            finished = new BooleanFilter();
        }
        return finished;
    }

    public void setFinished(BooleanFilter finished) {
        this.finished = finished;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SpendingCriteria that = (SpendingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(payer, that.payer) &&
            Objects.equals(nameCost, that.nameCost) &&
            Objects.equals(cost, that.cost) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(paid, that.paid) &&
            Objects.equals(pending, that.pending) &&
            Objects.equals(finished, that.finished) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, payer, nameCost, cost, descripcion, paid, pending, finished, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpendingCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (payer != null ? "payer=" + payer + ", " : "") +
            (nameCost != null ? "nameCost=" + nameCost + ", " : "") +
            (cost != null ? "cost=" + cost + ", " : "") +
            (descripcion != null ? "descripcion=" + descripcion + ", " : "") +
            (paid != null ? "paid=" + paid + ", " : "") +
            (pending != null ? "pending=" + pending + ", " : "") +
            (finished != null ? "finished=" + finished + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
