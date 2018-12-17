package org.elder.sourcerer.samples.todo.command

/**
 * Immutable write side representation of a TODO item.
 */
data class TodoItem(
        val createdBy: String? = null,
        val description: String? = null,
        val assignedTo: String? = null,
        val isCompleted: Boolean = false,
        val isDeleted: Boolean = false,
        val comments : List<String> = listOf()
)
