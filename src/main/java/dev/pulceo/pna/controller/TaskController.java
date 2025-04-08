package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.task.CreateNewTaskOnPnaDTO;
import dev.pulceo.pna.dto.task.CreateNewTaskOnPnaResponseDTO;
import dev.pulceo.pna.exception.TaskServiceException;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // TODO: Get task by id

    // TODO: Get task list by id

    @PostMapping("")
    public ResponseEntity<CreateNewTaskOnPnaResponseDTO> createNewTaskOnPna(@Valid @RequestBody CreateNewTaskOnPnaDTO createNewTaskOnPnaDTO) throws TaskServiceException, InterruptedException {
        Task task = this.taskService.createTask(Task.fromCreateNewTaskOnPnaDTO(createNewTaskOnPnaDTO));
        this.taskService.queueForScheduling(task.getUuid().toString());
        return ResponseEntity.status(200).body(CreateNewTaskOnPnaResponseDTO.fromTask(task));
    }

    // Exception Handler
    @ExceptionHandler(value = TaskServiceException.class)
    public ResponseEntity<CustomErrorResponse> handleTaskServiceException(TaskServiceException taskServiceException) {
        CustomErrorResponse error = new CustomErrorResponse("BAD_REQUEST", taskServiceException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrorMsg(taskServiceException.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
