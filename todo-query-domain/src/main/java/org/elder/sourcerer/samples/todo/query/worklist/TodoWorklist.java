package org.elder.sourcerer.samples.todo.query.worklist;

import org.elder.sourcerer.samples.todo.query.converters.JsonConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * The todo worklist is a summary of all items that are left to do (not yet done) for a particular
 * assignee.
 */
@Entity
public class TodoWorklist {
    public static class TasksConverter extends JsonConverter<List<TodoSummary>> {
    }

    public static class TodoSummary {
        private String todoId;
        private String todoDescription;

        public TodoSummary() {
        }

        public TodoSummary(final String todoId, final String todoDescription) {
            this.todoId = todoId;
            this.todoDescription = todoDescription;
        }

        public String getTodoId() {
            return todoId;
        }

        public void setTodoId(final String todoId) {
            this.todoId = todoId;
        }

        public String getTodoDescription() {
            return todoDescription;
        }

        public void setTodoDescription(final String todoDescription) {
            this.todoDescription = todoDescription;
        }
    }

    @Id
    private String assignee;

    @Convert(converter = TasksConverter.class)
    private List<TodoSummary> tasks;

    private String subscriptionVersion;

    public TodoWorklist() {
    }

    public TodoWorklist(final String assignee) {
        this.assignee = assignee;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(final String assignee) {
        this.assignee = assignee;
    }

    public List<TodoSummary> getTasks() {
        return tasks;
    }

    public void setTasks(final List<TodoSummary> tasks) {
        this.tasks = tasks;
    }

    public String getSubscriptionVersion() {
        return subscriptionVersion;
    }

    public void setSubscriptionVersion(final String subscriptionVersion) {
        this.subscriptionVersion = subscriptionVersion;
    }

    @Override
    public String toString() {
        return "TodoWorklist{" +
                "assignee='" + assignee + '\'' +
                ", tasks=" + tasks +
                ", subscriptionVersion='" + subscriptionVersion + '\'' +
                '}';
    }
}
