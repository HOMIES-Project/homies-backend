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
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.homies.app.domain.Task} entity. This class is used
 * in {@link com.homies.app.web.rest.TaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TaskCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter taskName;

    private LocalDateFilter dataCreate;

    private LocalDateFilter dataEnd;

    private StringFilter description;

    private BooleanFilter cancel;

    private StringFilter puntuacion;

    private LongFilter taskListId;

    private LongFilter userDataId;

    private LongFilter userCreatorId;

    private LongFilter userAssignedId;

    private Boolean distinct;

    public TaskCriteria() {}

    public TaskCriteria(TaskCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.taskName = other.taskName == null ? null : other.taskName.copy();
        this.dataCreate = other.dataCreate == null ? null : other.dataCreate.copy();
        this.dataEnd = other.dataEnd == null ? null : other.dataEnd.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.cancel = other.cancel == null ? null : other.cancel.copy();
        this.puntuacion = other.puntuacion == null ? null : other.puntuacion.copy();
        this.taskListId = other.taskListId == null ? null : other.taskListId.copy();
        this.userDataId = other.userDataId == null ? null : other.userDataId.copy();
        this.userCreatorId = other.userCreatorId == null ? null : other.userCreatorId.copy();
        this.userAssignedId = other.userAssignedId == null ? null : other.userAssignedId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TaskCriteria copy() {
        return new TaskCriteria(this);
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

    public StringFilter getTaskName() {
        return taskName;
    }

    public StringFilter taskName() {
        if (taskName == null) {
            taskName = new StringFilter();
        }
        return taskName;
    }

    public void setTaskName(StringFilter taskName) {
        this.taskName = taskName;
    }

    public LocalDateFilter getDataCreate() {
        return dataCreate;
    }

    public LocalDateFilter dataCreate() {
        if (dataCreate == null) {
            dataCreate = new LocalDateFilter();
        }
        return dataCreate;
    }

    public void setDataCreate(LocalDateFilter dataCreate) {
        this.dataCreate = dataCreate;
    }

    public LocalDateFilter getDataEnd() {
        return dataEnd;
    }

    public LocalDateFilter dataEnd() {
        if (dataEnd == null) {
            dataEnd = new LocalDateFilter();
        }
        return dataEnd;
    }

    public void setDataEnd(LocalDateFilter dataEnd) {
        this.dataEnd = dataEnd;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BooleanFilter getCancel() {
        return cancel;
    }

    public BooleanFilter cancel() {
        if (cancel == null) {
            cancel = new BooleanFilter();
        }
        return cancel;
    }

    public void setCancel(BooleanFilter cancel) {
        this.cancel = cancel;
    }

    public StringFilter getPuntuacion() {
        return puntuacion;
    }

    public StringFilter puntuacion() {
        if (puntuacion == null) {
            puntuacion = new StringFilter();
        }
        return puntuacion;
    }

    public void setPuntuacion(StringFilter puntuacion) {
        this.puntuacion = puntuacion;
    }

    public LongFilter getTaskListId() {
        return taskListId;
    }

    public LongFilter taskListId() {
        if (taskListId == null) {
            taskListId = new LongFilter();
        }
        return taskListId;
    }

    public void setTaskListId(LongFilter taskListId) {
        this.taskListId = taskListId;
    }

    public LongFilter getUserDataId() {
        return userDataId;
    }

    public LongFilter userDataId() {
        if (userDataId == null) {
            userDataId = new LongFilter();
        }
        return userDataId;
    }

    public void setUserDataId(LongFilter userDataId) {
        this.userDataId = userDataId;
    }

    public LongFilter getUserCreatorId() {
        return userCreatorId;
    }

    public LongFilter userCreatorId() {
        if (userCreatorId == null) {
            userCreatorId = new LongFilter();
        }
        return userCreatorId;
    }

    public void setUserCreatorId(LongFilter userCreatorId) {
        this.userCreatorId = userCreatorId;
    }

    public LongFilter getUserAssignedId() {
        return userAssignedId;
    }

    public LongFilter userAssignedId() {
        if (userAssignedId == null) {
            userAssignedId = new LongFilter();
        }
        return userAssignedId;
    }

    public void setUserAssignedId(LongFilter userAssignedId) {
        this.userAssignedId = userAssignedId;
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
        final TaskCriteria that = (TaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(taskName, that.taskName) &&
            Objects.equals(dataCreate, that.dataCreate) &&
            Objects.equals(dataEnd, that.dataEnd) &&
            Objects.equals(description, that.description) &&
            Objects.equals(cancel, that.cancel) &&
            Objects.equals(puntuacion, that.puntuacion) &&
            Objects.equals(taskListId, that.taskListId) &&
            Objects.equals(userDataId, that.userDataId) &&
            Objects.equals(userCreatorId, that.userCreatorId) &&
            Objects.equals(userAssignedId, that.userAssignedId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            taskName,
            dataCreate,
            dataEnd,
            description,
            cancel,
            puntuacion,
            taskListId,
            userDataId,
            userCreatorId,
            userAssignedId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (taskName != null ? "taskName=" + taskName + ", " : "") +
            (dataCreate != null ? "dataCreate=" + dataCreate + ", " : "") +
            (dataEnd != null ? "dataEnd=" + dataEnd + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (cancel != null ? "cancel=" + cancel + ", " : "") +
            (puntuacion != null ? "puntuacion=" + puntuacion + ", " : "") +
            (taskListId != null ? "taskListId=" + taskListId + ", " : "") +
            (userDataId != null ? "userDataId=" + userDataId + ", " : "") +
            (userCreatorId != null ? "userCreatorId=" + userCreatorId + ", " : "") +
            (userAssignedId != null ? "userAssignedId=" + userAssignedId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
