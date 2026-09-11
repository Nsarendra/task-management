package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required.")
    @Size(min = 3, max = 100, message = "Title must contain 3 to 100 characters.")
    private String title;

    @NotBlank(message = "Description is required.")
    @Size(min = 5, max = 500, message = "Description must contain 5 to 500 characters.")
    private String description;

    @NotNull(message = "Due date is required.")
    @FutureOrPresent(message = "Due date cannot be in the past.")
    private LocalDate dueDate;

    private TaskStatus status;

}
