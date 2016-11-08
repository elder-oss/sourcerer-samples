package org.elder.sourcerer.samples.todo.query.details

import java.time.Instant

data class TodoDetailsDto(
        val id : String,
        val creator : String,
        val description : String,
        val assignee : String?,
        val completed : Boolean,
        val completedAt : Instant?)
