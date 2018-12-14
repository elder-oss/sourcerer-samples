package org.elder.sourcerer.samples.todo.events

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.elder.sourcerer2.EventType
import java.time.Instant

/**
 * Base class for all events applicable to TodoItem aggregates.
 */
@EventType(repositoryName = "todoItemEvent")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(name = "created", value = TodoItemEvent.Created::class),
        JsonSubTypes.Type(name = "markedDone", value = TodoItemEvent.MarkedDone::class),
        JsonSubTypes.Type(name = "markedUndone", value = TodoItemEvent.MarkedUndone::class),
        JsonSubTypes.Type(name = "deleted", value = TodoItemEvent.Deleted::class),
        JsonSubTypes.Type(name = "assignedTo", value = TodoItemEvent.AssignedTo::class),
        JsonSubTypes.Type(name = "commentAdded", value = TodoItemEvent.CommentAdded::class))
sealed class TodoItemEvent() {
    class Created(val createdBy: String, val description: String) : TodoItemEvent()
    class AssignedTo(val assignee: String) : TodoItemEvent()
    class MarkedDone() : TodoItemEvent()
    class MarkedUndone() : TodoItemEvent()
    class Deleted() : TodoItemEvent()
    class CommentAdded(val comment : String) : TodoItemEvent()
}
