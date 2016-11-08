package org.elder.sourcerer.samples.todo.materializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import org.elder.sourcerer.DefaultEventSubscriptionFactory;
import org.elder.sourcerer.EventRepository;
import org.elder.sourcerer.EventSubscription;
import org.elder.sourcerer.EventSubscriptionFactory;
import org.elder.sourcerer.esjc.EventStoreEsjcEventRepositoryFactory;
import org.elder.sourcerer.samples.todo.events.TodoItemEvent;
import org.elder.sourcerer.samples.todo.query.converters.JsonConverter;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) throws Exception {
        logger.info("Starting materializer ...");

        // Create H2 web server for inspections and configure persistence
        Server webServer = Server.createWebServer();
        webServer.start();

        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("default");

        // Configure Jackson with support for Kotlin
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new KotlinModule());
        JsonConverter.setObjectMapper(objectMapper);

        // Configure sourcerer and EventStore connection
        EventStore eventStore = EventStoreBuilder
                .newBuilder()
                .userCredentials("admin", "changeit")
                .singleNodeAddress("localhost", 1113)
                .failOnNoServerResponseEnabled()
                .build();

        EventStoreEsjcEventRepositoryFactory repositoryFactory =
                new EventStoreEsjcEventRepositoryFactory(
                        eventStore,
                        objectMapper,
                        "sourcerer_todo");

        EventRepository<TodoItemEvent> todoEventRepository =
                repositoryFactory.getEventRepository(TodoItemEvent.class);

        EventSubscriptionFactory<TodoItemEvent> todoSubscriptionFactory =
                new DefaultEventSubscriptionFactory<>(todoEventRepository);

        // Create and start materializer subscription for TODO item details view
        TodoItemDetailsMaterializer detailsSubscriptionHandler
                = new TodoItemDetailsMaterializer(entityManagerFactory);
        EventSubscription detailsSubscription
                = todoSubscriptionFactory.fromSubscriptionHandler(detailsSubscriptionHandler);
        detailsSubscription.setPositionSource(detailsSubscriptionHandler);
        detailsSubscription.start();

        // Create and start materializer subscription for worklists
        TodoWorklistMaterializer worklistSubscriptionHandler
                = new TodoWorklistMaterializer(entityManagerFactory);
        EventSubscription worklistSubscription
                = todoSubscriptionFactory.fromSubscriptionHandler(worklistSubscriptionHandler);
        worklistSubscription.setPositionSource(worklistSubscriptionHandler);
        worklistSubscription.start();

        logger.info("Materializer running");
    }
}
