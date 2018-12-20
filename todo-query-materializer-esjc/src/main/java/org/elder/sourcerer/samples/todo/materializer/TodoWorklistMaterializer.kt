package org.elder.sourcerer.samples.todo.materializer

import org.elder.sourcerer2.EventRecord
import org.elder.sourcerer2.EventSubscriptionPositionSource
import org.elder.sourcerer.samples.todo.events.TodoItemEvent
import org.elder.sourcerer.samples.todo.query.worklist.TodoItemState
import org.elder.sourcerer.samples.todo.query.worklist.TodoWorklist
import org.elder.sourcerer2.RepositoryVersion
import java.util.ArrayList
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class TodoWorklistMaterializer
@Inject
constructor(entityManagerFactory: EntityManagerFactory) :
        MaterializerBase<TodoItemEvent>(
                entityManagerFactory,
                TodoItemDetailsMaterializer.logger)
        , EventSubscriptionPositionSource {
    override fun processEvents(
            eventRecords: List<EventRecord<TodoItemEvent>>,
            entityManager: EntityManager) {
        eventRecords.map { processEvent(it, entityManager) }
    }

    private fun processEvent(
            eventRecord: EventRecord<TodoItemEvent>,
            entityManager: EntityManager
    ) {
        val todoId = eventRecord.streamId.identifier
        val event = eventRecord.event
        val eventVersion = eventRecord.repositoryVersion!!
        val existingState = entityManager.find(TodoItemState::class.java, todoId)
        val state = existingState ?: TodoItemState(todoId)
        val currentAssignee = state.assignee

        when (event) {
            is TodoItemEvent.Created -> {
                state.description = event.description
                entityManager.persist(state)
            }
            is TodoItemEvent.AssignedTo -> {
                if (event.assignee != currentAssignee) {
                    if (currentAssignee != null) {
                        removeItem(entityManager, currentAssignee, todoId, eventVersion)
                    }
                    addItem(entityManager, event.assignee, todoId, state.description, eventVersion)
                    state.assignee = event.assignee
                    entityManager.persist(state)
                }
            }
            is TodoItemEvent.MarkedDone -> {
                if (currentAssignee != null) {
                    removeItem(entityManager, currentAssignee, todoId, eventVersion)
                }
            }
            is TodoItemEvent.MarkedUndone -> {
                if (currentAssignee != null) {
                    addItem(entityManager, currentAssignee, todoId, state.description, eventVersion)
                }
            }
            is TodoItemEvent.Deleted -> {
                if (currentAssignee != null) {
                    removeItem(entityManager, currentAssignee, todoId, eventVersion)
                }
                if (existingState != null) {
                    entityManager.remove(existingState)
                }
            }
        }
    }

    private fun removeItem(
            entityManager: EntityManager,
            assignee: String,
            todoId: String,
            subscriptionVersion: RepositoryVersion
    ) {
        val worklist = entityManager.find(TodoWorklist::class.java, assignee)
        worklist.tasks = worklist.tasks?.filter { it.todoId != todoId }
        worklist.subscriptionVersion = subscriptionVersion.version
        entityManager.persist(worklist)
    }

    private fun addItem(
            entityManager: EntityManager,
            assignee: String,
            todoId: String,
            description: String?,
            subscriptionVersion: RepositoryVersion
    ) {
        val worklist = entityManager.find(TodoWorklist::class.java, assignee)
                ?: TodoWorklist(assignee)
        val currentTasks = worklist.tasks ?: listOf<TodoWorklist.TodoSummary>()
        val newTasks = ArrayList(currentTasks.filter { it.todoId != todoId })
        newTasks.add(TodoWorklist.TodoSummary(todoId, description))
        worklist.subscriptionVersion = subscriptionVersion.version
        worklist.tasks = newTasks
        entityManager.persist(worklist)
    }

    override fun getSubscriptionPosition(entityManager: EntityManager): String? {
        val cq = entityManager.criteriaBuilder
        var query = cq.createQuery(String::class.java)
        val root = query.from(TodoWorklist::class.java)
        query = query.select(cq.greatest(root.get("subscriptionVersion")))
        return entityManager.createQuery(query).singleResult
    }
}