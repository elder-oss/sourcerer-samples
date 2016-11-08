package org.elder.sourcerer.samples.todo.query.details;

import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = TodoItemDetails.class, idClass = String.class)
public interface TodoDetailsRepository {
    TodoItemDetails findOne(String todoItemId);

    Iterable<TodoItemDetails> findAll();
}
