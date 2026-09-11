package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskRequest taskRequest;
    private Task task;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Spring Boot Assignment");
        taskRequest.setDescription("Complete REST API assignment");
        taskRequest.setDueDate(LocalDate.of(2026, 9, 20));

        task = Task.builder()
                .id(1L)
                .title("Spring Boot Assignment")
                .description("Complete REST API assignment")
                .dueDate(LocalDate.of(2026, 9, 20))
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskResponse = TaskResponse.builder()
                .id(1L)
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    @Test
    void createTask_ShouldCreateTaskSuccessfully() {

        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals("Spring Boot Assignment", response.getTitle());
        assertEquals(TaskStatus.PENDING, response.getStatus());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_ShouldCreateTaskWithInProgressStatus() {

        taskRequest.setStatus(TaskStatus.IN_PROGRESS);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskResponse.setStatus(TaskStatus.IN_PROGRESS);

        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse response = taskService.createTask(taskRequest);

        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
    }

    @Test
    void createTask_ShouldSetCreatedAndUpdatedTime() {

        Task newTask = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .build();

        when(taskMapper.toEntity(taskRequest)).thenReturn(newTask);
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        taskService.createTask(taskRequest);

        verify(taskRepository).save(argThat(savedTask ->
                savedTask.getCreatedAt() != null &&
                        savedTask.getUpdatedAt() != null &&
                        savedTask.getStatus() == TaskStatus.PENDING));
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .description("Second task")
                .dueDate(LocalDate.of(2026, 9, 25))
                .status(TaskStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskResponse response2 = TaskResponse.builder()
                .id(2L)
                .title("Task 2")
                .description("Second task")
                .dueDate(LocalDate.of(2026, 9, 25))
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task, task2));

        when(taskMapper.toResponse(task)).thenReturn(taskResponse);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        var response = taskService.getAllTasks();

        assertEquals(2, response.size());

        verify(taskRepository).findAll();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList() {

        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        var response = taskService.getAllTasks();

        assertTrue(response.isEmpty());

        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_ShouldReturnTask() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        TaskResponse response = taskService.getTaskById(1L);

        assertEquals(1L, response.getId());

        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {

        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getTaskById(10L));

        assertEquals("Task not found with id: 10", exception.getMessage());
    }

    @Test
    void updateTask_ShouldUpdateTaskSuccessfully() {

        taskRequest.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskResponse.setStatus(TaskStatus.IN_PROGRESS);

        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.updateTask(1L, taskRequest);

        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());

        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_ShouldKeepOldStatus_WhenStatusIsNull() {

        task.setStatus(TaskStatus.PENDING);
        taskRequest.setStatus(null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        taskService.updateTask(1L, taskRequest);

        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskDoesNotExist() {

        when(taskRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(50L, taskRequest));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_ShouldDeleteTaskSuccessfully() {

        when(taskRepository.existsById(1L)).thenReturn(true);

        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {

        when(taskRepository.existsById(5L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.deleteTask(5L));

        assertEquals("Task not found with id: 5", exception.getMessage());

        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void markTaskAsComplete_ShouldMarkTaskCompleted() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        task.setStatus(TaskStatus.COMPLETED);
        taskResponse.setStatus(TaskStatus.COMPLETED);

        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.markTaskAsComplete(1L);

        assertEquals(TaskStatus.COMPLETED, response.getStatus());

        verify(taskRepository).save(task);
    }

    @Test
    void markTaskAsComplete_ShouldUpdateUpdatedAt() {

        LocalDateTime oldTime = task.getUpdatedAt();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        taskService.markTaskAsComplete(1L);

        assertTrue(task.getUpdatedAt().isAfter(oldTime));
    }

    @Test
    void markTaskAsComplete_ShouldThrowException_WhenTaskNotFound() {

        when(taskRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.markTaskAsComplete(100L));

        assertEquals("Task not found with id: 100", exception.getMessage());

        verify(taskRepository, never()).save(any());
    }
}
