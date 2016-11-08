package org.elder.sourcerer.samples.todo.query.worklist;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api("TODO item worklist service - extracting TODO items per assignee")
public class TodoWorklistQueryService {
    private final TodoWorklistRepository worklistRepository;

    @Autowired
    public TodoWorklistQueryService(final TodoWorklistRepository worklistRepository) {
        this.worklistRepository = worklistRepository;
    }

    @RequestMapping(value = "/todo/query/worklist", method = RequestMethod.GET)
    public List<TodoWorklist.TodoSummary> getWorklist(@RequestParam final String assignee) {
        TodoWorklist worklist = worklistRepository.findOne(assignee);
        if (worklist == null) {
            return new ArrayList<>();
        }

        return worklist.getTasks();
    }
}
