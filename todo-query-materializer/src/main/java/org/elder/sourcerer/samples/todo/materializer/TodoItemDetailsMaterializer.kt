package org.elder.sourcerer.samples.todo.materializer

import org.elder.sourcerer2.EventRecord
import org.elder.sourcerer2.EventSubscriptionPositionSource
import org.elder.sourcerer.samples.todo.events.TodoItemEvent
import org.elder.sourcerer.samples.todo.query.details.TodoItemDetails
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class TodoItemDetailsMaterializer
@Inject
constructor(entityManagerFactory: EntityManagerFactory) :
        MaterializerBase<TodoItemEvent>(
                entityManagerFactory,
                TodoItemDetailsMaterializer.logger)
        , EventSubscriptionPositionSource {
    companion object {
        val logger = LoggerFactory.getLogger(TodoItemDetailsMaterializer::class.java)
    }

    override fun processEvents(
            eventRecords: List<EventRecord<TodoItemEvent>>,
            entityManager: EntityManager) {
        eventRecords.map { processEvent(it, entityManager) }
    }

    private fun processEvent(
            eventRecord: EventRecord<TodoItemEvent>,
            entityManager: EntityManager) {
        val event = eventRecord.event
        val existingDetails = entityManager.find(TodoItemDetails::class.java, eventRecord.streamId)
        val details = existingDetails ?: TodoItemDetails(eventRecord.streamId)

        when (event) {
            is TodoItemEvent.Created -> {
                details.creationTimestamp = eventRecord.timestamp
                details.creator = event.createdBy
                details.description = event.description
            }
            is TodoItemEvent.AssignedTo -> {
                details.assignee = event.assignee
            }
            is TodoItemEvent.MarkedDone -> {
                details.completionTimestamp = eventRecord.timestamp
                details.isCompleted = true;
            }
            is TodoItemEvent.MarkedUndone -> {
                details.isCompleted = false;
            }
            is TodoItemEvent.Deleted -> {
                if (existingDetails != null) {
                    entityManager.remove(details)
                    return
                }
            }
        }

        details.version = eventRecord.streamVersion
        entityManager.persist(details)
    }

    override fun getSubscriptionPosition(entityManager: EntityManager): Int? {
        val cq = entityManager.criteriaBuilder
        var query = cq.createQuery(Int::class.java)
        val root = query.from(TodoItemDetails::class.java)
        query = query.select(cq.max(root.get("version")))
        return entityManager.createQuery(query).singleResult
    }
}
