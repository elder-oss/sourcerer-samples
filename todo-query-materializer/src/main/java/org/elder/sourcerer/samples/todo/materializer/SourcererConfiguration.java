package org.elder.sourcerer.samples.todo.materializer;

import org.elder.sourcerer.samples.todo.events.TodoItemEvent;
import org.elder.sourcerer.spring.SourcererEventConfiguration;

public class SourcererConfiguration extends SourcererEventConfiguration<TodoItemEvent> {
    public SourcererConfiguration() {
        super(TodoItemEvent.class);
    }
}
