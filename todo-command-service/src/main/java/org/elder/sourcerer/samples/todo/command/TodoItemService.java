package org.elder.sourcerer.samples.todo.command;

import io.swagger.annotations.Api;
import org.elder.sourcerer.Command;
import org.elder.sourcerer.CommandFactory;
import org.elder.sourcerer.CommandResponse;
import org.elder.sourcerer.CommandResult;
import org.elder.sourcerer.ExpectedVersion;
import org.elder.sourcerer.Operations;
import org.elder.sourcerer.samples.todo.events.TodoItemEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Api
@RestController
public class TodoItemService {
    private final CommandFactory<TodoItem, TodoItemEvent> commandFactory;
    private final TodoItemOperations operations;

    public TodoItemService(
            final CommandFactory<TodoItem, TodoItemEvent> commandFactory) {
        this(commandFactory, new TodoItemOperations());
    }

    @Autowired
    public TodoItemService(
            final CommandFactory<TodoItem, TodoItemEvent> commandFactory,
            final TodoItemOperations operations) {
        this.commandFactory = commandFactory;
        this.operations = operations;
    }

    @RequestMapping(value = "/todo/command/create", method = RequestMethod.POST)
    public CommandResponse createTodo(
            @RequestBody final TodoItemOperations.CreateParams createParams) {
        String todoId = UUID.randomUUID().toString();
        CommandResult<TodoItemEvent> result = commandFactory
                .fromOperation(Operations.constructorOf(operations::create))
                .setAggregateId(todoId)
                .setArguments(createParams)
                .run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/markdone", method = RequestMethod.POST)
    public CommandResponse markDone(
            @RequestParam final String todoId,
            @RequestParam(required = false) final Integer expectedVersion) {
        Command<TodoItem, Object, TodoItemEvent> command = commandFactory
                .fromOperation(Operations.appendOf(operations::markDone))
                .setAggregateId(todoId);

        if (expectedVersion != null) {
            command.setExpectedVersion(ExpectedVersion.exactly(expectedVersion));
            command.setAtomic(true);
        }

        CommandResult<TodoItemEvent> result = command.run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/toggledone", method = RequestMethod.POST)
    public CommandResponse toggleDone(@RequestParam final String todoId) {
        CommandResult<TodoItemEvent> result = commandFactory
                .fromOperation(Operations.updateOf(operations::toggleDone))
                .setAggregateId(todoId)
                .setAtomic(true)
                .run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/assign", method = RequestMethod.POST)
    public CommandResponse assign(
            @RequestParam final String todoId,
            @RequestParam final String assignee) {
        CommandResult<TodoItemEvent> result = commandFactory
                .fromOperation(Operations.appendOf(operations::assign))
                .setAggregateId(todoId)
                .setArguments(new TodoItemOperations.AssignParams(assignee))
                .run();
        return CommandResponse.of(result);
    }
}
