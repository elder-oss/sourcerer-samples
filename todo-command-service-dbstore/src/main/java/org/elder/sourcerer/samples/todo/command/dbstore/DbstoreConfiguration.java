package org.elder.sourcerer.samples.todo.command.dbstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.elder.sourcerer2.EventRepositoryFactory;
import org.elder.sourcerer2.dbstore.DbstoreEventRepositoryFactory;
import org.elder.sourcerer2.dbstore.DbstoreEventStore;
import org.elder.sourcerer2.dbstore.jdbc.JdbcEventStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Configuration
public class DbstoreConfiguration {
    @Bean
    @Singleton
    @Qualifier("eventsDbSource")
    public DataSource getEventsDataSource(
            @Value("${sourcerer.dbstore.jdbc.dbUrl:}") final String url,
            @Value("${sourcerer.dbstore.jdbc.dbUsername:}") final String username,
            @Value("${sourcerer.dbstore.jdbc.dbPassword:}") final String password
    ) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public DbstoreEventStore getEventStoreConnection(
            @Qualifier("eventsDbSource")
            final DataSource dataSource,
            @Value("${sourcerer.dbstore.shards:4}")
            final int shards,
            @Value("${sourcerer.dbstore.jdbc.eventsTableName:events}")
            final String eventsTableName
    ) {
        return new JdbcEventStore(
                dataSource,
                eventsTableName,
                shards,
                2048
        );
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public EventRepositoryFactory getEventRepositoryFactory(
            final DbstoreEventStore eventStore,
            final ObjectMapper objectMapper,
            @Value("${sourcerer.dbstore.eventstore.shards:4}") final int shards,
            @Value("${sourcerer.dbstore.namespace}") final String namespace
    ) {
        return new DbstoreEventRepositoryFactory(
                eventStore,
                shards,
                objectMapper,
                namespace.trim()
        );
    }
}
