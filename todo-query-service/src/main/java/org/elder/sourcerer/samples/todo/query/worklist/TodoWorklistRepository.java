package org.elder.sourcerer.samples.todo.query.worklist;

import org.elder.sourcerer.samples.todo.query.details.TodoItemDetails;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = TodoWorklist.class, idClass = String.class)
public interface TodoWorklistRepository {
    TodoWorklist findOne(String assignee);
}
