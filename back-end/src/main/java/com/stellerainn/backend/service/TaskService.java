package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.Task;
import com.stellerainn.backend.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    public List<Task> getTasksByUserId(Long userId) {
        return taskMapper.findByUserId(userId);
    }

    public Task createTask(Task task) {
        taskMapper.insert(task);
        return task;
    }

    public Task updateTask(Task task) {
        taskMapper.update(task);
        return taskMapper.findById(task.getId());
    }

    public void deleteTask(Long id) {
        taskMapper.delete(id);
    }
    
    public Task getTaskById(Long id) {
        return taskMapper.findById(id);
    }
}
