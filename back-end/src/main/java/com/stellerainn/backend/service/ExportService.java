package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.PomodoroRecord;
import com.stellerainn.backend.entity.Task;
import com.stellerainn.backend.entity.Tag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private PomodoroService pomodoroService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ByteArrayInputStream exportTasksToCsv(Long userId) {
        List<Task> tasks = taskService.getTasksByUserId(userId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8), 
             CSVFormat.DEFAULT.builder().setHeader("ID", "Title", "Description", "Status", "Priority", "Tags", "Due Date", "Created At").build())) {

            for (Task task : tasks) {
                String tags = task.getTags() != null 
                    ? task.getTags().stream().map(Tag::getName).collect(Collectors.joining(",")) 
                    : "";
                
                csvPrinter.printRecord(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getPriority(),
                    tags,
                    task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : "",
                    task.getCreatedAt() != null ? task.getCreatedAt().format(DATE_FORMATTER) : ""
                );
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export tasks to CSV: " + e.getMessage());
        }
    }

    public ByteArrayInputStream exportPomodoroToCsv(Long userId) {
        List<PomodoroRecord> records = pomodoroService.getRecordsByUserId(userId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8), 
             CSVFormat.DEFAULT.builder().setHeader("ID", "Task ID", "Start Time", "End Time", "Duration (sec)", "Status").build())) {

            for (PomodoroRecord record : records) {
                csvPrinter.printRecord(
                    record.getId(),
                    record.getTaskId() != null ? record.getTaskId() : "N/A",
                    record.getStartTime() != null ? record.getStartTime().format(DATE_FORMATTER) : "",
                    record.getEndTime() != null ? record.getEndTime().format(DATE_FORMATTER) : "",
                    record.getDurationSeconds(),
                    record.getStatus()
                );
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export pomodoro records to CSV: " + e.getMessage());
        }
    }
}
