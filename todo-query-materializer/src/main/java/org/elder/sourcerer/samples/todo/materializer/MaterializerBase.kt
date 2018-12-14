package org.elder.sourcerer.samples.todo.materializer

import org.elder.sourcerer2.EventRecord
import org.elder.sourcerer2.EventSubscriptionHandlerBase
import org.elder.sourcerer2.EventSubscriptionPositionSource
import org.slf4j.Logger
import java.util.function.Function
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction

/**
 * Base class for sourcerer subscription handlers that materializes state using a JPA EntityManager.
 */
abstract class MaterializerBase<T>(
        private val entityManagerFactory: EntityManagerFactory,
        logger: Logger) : EventSubscriptionHandlerBase<T>(logger)
                          , EventSubscriptionPositionSource {
    override fun processEvents(eventRecords: List<EventRecord<T>>) {
        withEntityManager(Function<EntityManager, Unit> {
            entityManager ->
            this@MaterializerBase.processEvents(eventRecords, entityManager)
        })
    }

    override fun getSubscriptionPosition(): Int? {
        return withEntityManager({ getSubscriptionPosition(it) })
    }

    protected abstract fun getSubscriptionPosition(entityManager: EntityManager): Int?

    protected fun <U> withEntityManager(func: (EntityManager) -> U): U {
        var entityManager: EntityManager? = null
        try {
            entityManager = entityManagerFactory.createEntityManager()
            var transaction: EntityTransaction? = null
            try {
                transaction = entityManager.transaction
                transaction.begin()
                return func(entityManager)
            } catch (e: Exception) {
                transaction?.rollback()
                transaction = null
                throw e
            } finally {
                transaction?.commit()
            }
        } finally {
            entityManager?.close()
        }
    }

    protected fun <U> withEntityManager(func: Function<EntityManager, U>): U {
        return withEntityManager({ func.apply(it) })
    }

    /**
     * Process a batch of events using an entity manager.
     *
     * The entity manager will be alive and active for the duration of this call, but should not
     * be captured and used after the method has returned.
     */
    protected abstract fun processEvents(
            eventRecords: List<EventRecord<T>>,
            entityManager: EntityManager)
}
