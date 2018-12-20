package org.elder.sourcerer.samples.todo.materializer

import org.elder.sourcerer.samples.todo.events.TodoItemEvent
import org.elder.sourcerer.samples.todo.query.details.TodoItemDetails
import org.elder.sourcerer2.EventRecord
import org.elder.sourcerer2.EventSubscriptionPositionSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

class TodoItemDetailsMaterializer(entityManagerFactory: EntityManagerFactory) :
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
        logger.info("Processing {} for {} ({})", eventRecord.event, eventRecord.streamId, eventRecord.repositoryVersion)
        val event = eventRecord.event
        val existingDetails = entityManager.find(
                TodoItemDetails::class.java,
                eventRecord.streamId.identifier)
        val details = existingDetails ?: TodoItemDetails(eventRecord.streamId.identifier)

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

        details.aggregateVersion = eventRecord.streamVersion.version
        details.subscriptionVersion = eventRecord.repositoryVersion?.version
        entityManager.persist(details)
    }

    override fun getSubscriptionPosition(entityManager: EntityManager): String? {
        val cq = entityManager.criteriaBuilder
        var query = cq.createQuery(String::class.java)
        val root = query.from(TodoItemDetails::class.java)
        query = query.select(cq.greatest(root.get("subscriptionVersion")))
        return entityManager.createQuery(query).singleResult
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(TodoItemDetailsMaterializer::class.java)
    }
}
