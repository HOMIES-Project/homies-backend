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
 * Criteria class for the {@link com.homies.app.domain.TaskList} entity. This class is used
 * in {@link com.homies.app.web.rest.TaskListResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /task-lists?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TaskListCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nameList;

    private Boolean distinct;

    public TaskListCriteria() {}

    public TaskListCriteria(TaskListCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nameList = other.nameList == null ? null : other.nameList.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TaskListCriteria copy() {
        return new TaskListCriteria(this);
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

    public StringFilter getNameList() {
        return nameList;
    }

    public StringFilter nameList() {
        if (nameList == null) {
            nameList = new StringFilter();
        }
        return nameList;
    }

    public void setNameList(StringFilter nameList) {
        this.nameList = nameList;
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
        final TaskListCriteria that = (TaskListCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(nameList, that.nameList) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameList, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskListCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nameList != null ? "nameList=" + nameList + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
