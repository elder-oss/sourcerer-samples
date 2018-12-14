package org.elder.sourcerer.samples.todo.command;

import org.elder.sourcerer.samples.todo.events.TodoItemEvent;
import org.elder.sourcerer2.spring.SourcererCommandConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SourcererConfiguration
        extends SourcererCommandConfiguration<TodoItemEvent, TodoItem> {
    public SourcererConfiguration() {
        super(TodoItemEvent.class, TodoItem.class, new TodoItemProjection());
    }
}
