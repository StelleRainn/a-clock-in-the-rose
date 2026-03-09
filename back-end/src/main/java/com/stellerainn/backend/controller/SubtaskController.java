package com.stellerainn.backend.controller;

import com.stellerainn.backend.common.Result;
import com.stellerainn.backend.entity.Subtask;
import com.stellerainn.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public Result<Subtask> create(@RequestBody Subtask subtask) {
        return Result.success(taskService.addSubtask(subtask));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Subtask subtask) {
        subtask.setId(id);
        taskService.updateSubtask(subtask);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        taskService.deleteSubtask(id);
        return Result.success(null);
    }
}
