package org.elder.sourcerer.samples.todo.materializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.elder.sourcerer.samples.todo.events.TodoItemEvent;
import org.elder.sourcerer.samples.todo.query.converters.JsonConverter;
import org.elder.sourcerer2.DefaultEventSubscriptionFactory;
import org.elder.sourcerer2.EventRepository;
import org.elder.sourcerer2.EventRepositoryFactory;
import org.elder.sourcerer2.EventSubscription;
import org.elder.sourcerer2.EventSubscriptionFactory;
import org.elder.sourcerer2.dbstore.DbstoreEventRepositoryFactory;
import org.elder.sourcerer2.dbstore.DbstoreEventStore;
import org.elder.sourcerer2.dbstore.jdbc.JdbcEventStore;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

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

        // Configure sourcerer and DbStore/JDBC connection
        DataSource dbstoreDataSource = getEventsDataSource(
                "jdbc:mysql://localhost:3306/events_test?useTimezone=true&serverTimezone=UTC",
                "root",
                "pa55w0rd"
        );

        DbstoreEventStore dbstoreDataStore =
                getDbstoreEventStore(dbstoreDataSource, "events");

        EventRepositoryFactory repositoryFactory = getEventRepositoryFactory(
                dbstoreDataStore,
                objectMapper,
                4,
                "sourcerer_todo_2"
        );

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

    private static DataSource getEventsDataSource(
            final String url,
            final String username,
            final String password
    ) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }

    private static DbstoreEventStore getDbstoreEventStore(
            final DataSource dataSource,
            final String eventsTableName
    ) {
        return new JdbcEventStore(
                dataSource,
                eventsTableName,
                2048
        );
    }

    private static EventRepositoryFactory getEventRepositoryFactory(
            final DbstoreEventStore eventStore,
            final ObjectMapper objectMapper,
            final int shards,
            final String namespace
    ) {
        return new DbstoreEventRepositoryFactory(
                eventStore,
                objectMapper,
                namespace.trim(),
                shards
        );
    }
}
