package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
