package org.elder.sourcerer.samples.todo.command

import com.google.common.collect.ImmutableList
import org.elder.sourcerer.samples.todo.events.TodoItemEvent
import org.springframework.stereotype.Component

@Component
class TodoItemOperations {
    data class CreateParams(val createdBy: String, val description: String)
    data class AssignParams(val assignee: String)

    fun create(params: CreateParams): TodoItemEvent.Created {
        return TodoItemEvent.Created(params.createdBy, params.description)
    }

    fun markDone(): TodoItemEvent.MarkedDone {
        return TodoItemEvent.MarkedDone()
    }

    fun toggleDone(state: TodoItem): TodoItemEvent {
        if (state.isCompleted) {
            return TodoItemEvent.MarkedUndone()
        } else {
            return TodoItemEvent.MarkedDone()
        }
    }

    fun toggleDoneTwice(): ImmutableList<TodoItemEvent> {
        return ImmutableList.of(
                TodoItemEvent.MarkedDone(),
                TodoItemEvent.MarkedUndone())
    }

    fun assign(params: AssignParams): TodoItemEvent.AssignedTo {
        return TodoItemEvent.AssignedTo(params.assignee)
    }
}
