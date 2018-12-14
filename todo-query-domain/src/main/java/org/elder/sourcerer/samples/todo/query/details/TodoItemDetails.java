package org.elder.sourcerer.samples.todo.query.details;

import org.elder.sourcerer.samples.todo.query.converters.InstantJpaConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

/**
 * Materialized view of a single todo item when displayed with full details.
 */
@Entity
public class TodoItemDetails {
    @Id
    private String todoItemId;
    private String aggregateVersion;
    private String subscriptionVersion;
    private String creator;
    private String description;
    private String assignee;
    private boolean completed;
    @Convert(converter = InstantJpaConverter.class)
    private Instant creationTimestamp;
    @Convert(converter = InstantJpaConverter.class)
    private Instant completionTimestamp;

    public TodoItemDetails() {
    }

    public TodoItemDetails(final String todoItemId) {
        this.todoItemId = todoItemId;
    }

    public String getAggregateVersion() {
        return aggregateVersion;
    }

    public void setAggregateVersion(final String aggregateVersion) {
        this.aggregateVersion = aggregateVersion;
    }

    public String getSubscriptionVersion() {
        return subscriptionVersion;
    }

    public void setSubscriptionVersion(final String subscriptionVersion) {
        this.subscriptionVersion = subscriptionVersion;
    }

    public String getTodoItemId() {
        return todoItemId;
    }

    public void setTodoItemId(final String todoItemId) {
        this.todoItemId = todoItemId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(final boolean completed) {
        this.completed = completed;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(final Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Instant getCompletionTimestamp() {
        return completionTimestamp;
    }

    public void setCompletionTimestamp(final Instant completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
    }

    @Override
    public String toString() {
        return "TodoItemDetails{" +
                "todoItemId='" + todoItemId + '\'' +
                ", aggregateVersion='" + aggregateVersion + '\'' +
                ", subscriptionVersion='" + subscriptionVersion + '\'' +
                ", creator='" + creator + '\'' +
                ", description='" + description + '\'' +
                ", assignee='" + assignee + '\'' +
                ", completed=" + completed +
                ", creationTimestamp=" + creationTimestamp +
                ", completionTimestamp=" + completionTimestamp +
                '}';
    }
}
