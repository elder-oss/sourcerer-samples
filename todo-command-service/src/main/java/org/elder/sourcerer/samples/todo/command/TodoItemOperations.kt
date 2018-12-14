package org.elder.sourcerer.samples.todo.command

import com.google.common.collect.ImmutableList
import org.elder.sourcerer2.Aggregate
import org.elder.sourcerer2.ImmutableAggregate
import org.elder.sourcerer.samples.todo.events.TodoItemEvent
import org.springframework.stereotype.Component

@Component
class TodoItemOperations {
    data class CreateParams(val createdBy: String, val description: String)
    data class AssignParams(val assignee: String)
    data class AddCommentParams(val comment: String)

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

    /**
     * Example operation using an aggregate to apply events, offering a preview of the
     * resulting aggregate state.
     */
    fun addComment(
            aggregate: ImmutableAggregate<TodoItem, TodoItemEvent>,
            params: AddCommentParams)
            : Aggregate<TodoItem, TodoItemEvent> {
        val updatedAggregate = aggregate.apply(TodoItemEvent.CommentAdded(params.comment))
        // We can now perform operations on the updated state as needed and apply additional
        // operations as needed.
        return updatedAggregate
    }

    /**
     * Example operation using a mutable aggregate to apply events, offering a preview of the
     * resulting aggregate state.
     */
    fun closeWithComment(
            sourceAggregate: ImmutableAggregate<TodoItem, TodoItemEvent>,
            params: AddCommentParams)
            : Aggregate<TodoItem, TodoItemEvent> {
        val aggregate = sourceAggregate.toMutable()
        aggregate.apply(TodoItemEvent.CommentAdded(params.comment))
        if (!aggregate.state().isCompleted) {
            aggregate.apply(TodoItemEvent.MarkedDone())
        }
        return aggregate
    }
}
