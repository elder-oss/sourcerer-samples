package org.elder.sourcerer.samples.todo.command;

import io.swagger.annotations.Api;
import org.elder.sourcerer.samples.todo.events.TodoItemEvent;
import org.elder.sourcerer2.Command;
import org.elder.sourcerer2.CommandFactory;
import org.elder.sourcerer2.CommandResult;
import org.elder.sourcerer2.ExpectedVersion;
import org.elder.sourcerer2.Operations;
import org.elder.sourcerer2.StreamId;
import org.elder.sourcerer2.StreamVersion;
import org.elder.sourcerer2.extras.CommandResponse;
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
                .setAggregateId(StreamId.ofString(todoId))
                .setArguments(createParams)
                .run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/markdone", method = RequestMethod.POST)
    public CommandResponse markDone(
            @RequestParam final String todoId,
            @RequestParam(required = false) final String expectedVersion
    ) {
        Command<TodoItem, Object, TodoItemEvent> command = commandFactory
                .fromOperation(Operations.appendOf(operations::markDone))
                .setAggregateId(StreamId.ofString(todoId));

        if (expectedVersion != null) {
            command.setExpectedVersion(ExpectedVersion.exactly(StreamVersion.ofString(
                    expectedVersion)));
            command.setAtomic(true);
        }

        CommandResult<TodoItemEvent> result = command.run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/toggledone", method = RequestMethod.POST)
    public CommandResponse toggleDone(@RequestParam final String todoId) {
        CommandResult<TodoItemEvent> result = commandFactory
                .fromOperation(Operations.updateOf(operations::toggleDone))
                .setAggregateId(StreamId.ofString(todoId))
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
                .setAggregateId(StreamId.ofString(todoId))
                .setArguments(new TodoItemOperations.AssignParams(assignee))
                .run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/comment", method = RequestMethod.POST)
    public CommandResponse comment(
            @RequestParam final String todoId,
            @RequestParam final String comment) {
        CommandResult<TodoItemEvent> result = commandFactory
                .fromOperation(Operations.updateOf(operations::addComment))
                .setAggregateId(StreamId.ofString(todoId))
                .setArguments(new TodoItemOperations.AddCommentParams(comment))
                .run();
        return CommandResponse.of(result);
    }

    @RequestMapping(value = "/todo/command/commentAndClose", method = RequestMethod.POST)
    public CommandResponse commentAndClose(
            @RequestParam final String todoId,
            @RequestParam final String comment) {
        CommandResult<TodoItemEvent> result = commandFactory
                .fromOperation(Operations.updateOf(operations::closeWithComment))
                .setAggregateId(StreamId.ofString(todoId))
                .setArguments(new TodoItemOperations.AddCommentParams(comment))
                .run();
        return CommandResponse.of(result);
    }
}
