package org.elder.sourcerer.samples.todo.command

/**
 * Immutable write side representation of a TODO item.
 */
data class TodoItem(
        val createdBy: String,
        val description: String,
        val assignedTo: String?,
        val isCompleted: Boolean,
        val isDeleted: Boolean) {
    constructor(createdBy: String, description: String)
        : this(createdBy, description, null, false, false)
}
