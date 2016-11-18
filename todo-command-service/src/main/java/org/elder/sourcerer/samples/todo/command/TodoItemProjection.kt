package org.elder.sourcerer.samples.todo.command

import org.elder.sourcerer.AggregateProjection
import org.elder.sourcerer.samples.todo.events.TodoItemEvent

class TodoItemProjection : AggregateProjection<TodoItem, TodoItemEvent> {
    override fun empty(): TodoItem {
        return TodoItem()
    }

    override fun apply(id : String, state: TodoItem, eventData: TodoItemEvent): TodoItem {
        return when (eventData) {
            is TodoItemEvent.Created ->
                state.copy(
                        createdBy =  eventData.createdBy,
                        description = eventData.description)
            is TodoItemEvent.AssignedTo ->
                state.copy(assignedTo = eventData.assignee)
            is TodoItemEvent.MarkedDone -> state.copy(isCompleted = true)
            is TodoItemEvent.MarkedUndone -> state.copy(isCompleted = false)
            is TodoItemEvent.Deleted -> state.copy(isDeleted = true)
            is TodoItemEvent.CommentAdded ->
                state.copy(comments = state.comments + eventData.comment)
        }
    }
}
