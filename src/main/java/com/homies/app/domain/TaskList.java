package com.homies.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TaskList.
 */
@Entity
@Table(name = "task_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TaskList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(min = 3, max = 20)
    @Column(name = "name_list", length = 20)
    private String nameList;

    @JsonIgnoreProperties(value = { "userData", "userAdmin", "taskList" }, allowSetters = true)
    @OneToOne(mappedBy = "taskList")
    private Group group;

    @OneToMany(mappedBy = "taskList")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "taskList", "userData", "userCreator", "userAssigneds" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TaskList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameList() {
        return this.nameList;
    }

    public TaskList nameList(String nameList) {
        this.setNameList(nameList);
        return this;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        if (this.group != null) {
            this.group.setTaskList(null);
        }
        if (group != null) {
            group.setTaskList(this);
        }
        this.group = group;
    }

    public TaskList group(Group group) {
        this.setGroup(group);
        return this;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setTaskList(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setTaskList(this));
        }
        this.tasks = tasks;
    }

    public TaskList tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public TaskList addTask(Task task) {
        this.tasks.add(task);
        task.setTaskList(this);
        return this;
    }

    public TaskList removeTask(Task task) {
        this.tasks.remove(task);
        task.setTaskList(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskList)) {
            return false;
        }
        return id != null && id.equals(((TaskList) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskList{" +
            "id=" + getId() +
            ", nameList='" + getNameList() + "'" +
            "}";
    }
}
