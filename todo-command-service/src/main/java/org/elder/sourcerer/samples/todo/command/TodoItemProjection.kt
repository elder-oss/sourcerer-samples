package org.elder.sourcerer.samples.todo.command

import org.elder.sourcerer.AggregateProjection
import org.elder.sourcerer.samples.todo.events.TodoItemEvent

class TodoItemProjection : AggregateProjection<TodoItem, TodoItemEvent> {
    override fun apply(id : String, state: TodoItem?, eventData: TodoItemEvent): TodoItem {
        return when (eventData) {
            is TodoItemEvent.Created ->
                TodoItem(eventData.createdBy, eventData.description)
            is TodoItemEvent.AssignedTo ->
                state!!.copy(assignedTo = eventData.assignee)
            is TodoItemEvent.MarkedDone -> state!!.copy(isCompleted = true)
            is TodoItemEvent.MarkedUndone -> state!!.copy(isCompleted = false)
            is TodoItemEvent.Deleted -> state!!.copy(isDeleted = true)
        }
    }
}
