package org.elder.sourcerer.samples.todo.query.details;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.elder.sourcerer.samples.todo.query.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@Api("Query operations on TODO items")
public class TodoDetailsQueryService {
    private final TodoDetailsRepository repository;

    @Autowired
    public TodoDetailsQueryService(final TodoDetailsRepository repository) {
        this.repository = repository;
    }

    @ApiOperation(value = "Gets a Todo item via id")
    @RequestMapping(value = "/todo/query/details/{todoItemId}", method = RequestMethod.GET)
    public TodoDetailsDto getItemDetails(
            @ApiParam(value = "Id of the TODO item to retrieve",
                      required = true)
            @PathVariable final String todoItemId) {
        TodoItemDetails details = repository.findOne(todoItemId);
        if (details == null) {
            throw new NotFoundException();
        }

        return detailsToDto(details);
    }

    @ApiOperation(value = "Gets the details of all TODO items")
    @RequestMapping(value = "/todo/query/details", method = RequestMethod.GET)
    public List<TodoDetailsDto> getItemDetails() {
        Iterable<TodoItemDetails> items = repository.findAll();
        return StreamSupport
                .stream(items.spliterator(), false)
                .map(this::detailsToDto)
                .collect(Collectors.toList());
    }

    private TodoDetailsDto detailsToDto(final TodoItemDetails details) {
        return new TodoDetailsDto(
                details.getTodoItemId(),
                details.getCreator(),
                details.getDescription(),
                details.getAssignee(),
                details.isCompleted(),
                details.getCompletionTimestamp());
    }
}
