package org.elder.sourcerer.samples.todo.query.worklist;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Lookup of the worklist that a todo item is currently on, if any.
 */
@Entity
public class TodoItemState {
    @Id
    private String todoItemId;
    private String description;
    private String assignee;

    public TodoItemState() {
    }

    public TodoItemState(final String todoItemId) {
        this.todoItemId = todoItemId;
    }

    public String getTodoItemId() {
        return todoItemId;
    }

    public void setTodoItemId(final String todoItemId) {
        this.todoItemId = todoItemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(final String assignee) {
        this.assignee = assignee;
    }

    @Override
    public String toString() {
        return "TodoItemState{" +
                "todoItemId='" + todoItemId + '\'' +
                ", description='" + description + '\'' +
                ", assignee='" + assignee + '\'' +
                '}';
    }
}
