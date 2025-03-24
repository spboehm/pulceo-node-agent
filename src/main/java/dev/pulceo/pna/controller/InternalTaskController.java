package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.task.internal.UpdateTaskInternallyOnPNADTO;
import dev.pulceo.pna.dto.task.internal.UpdateTaskInternallyOnPNAResponseDTO;
import dev.pulceo.pna.exception.TaskServiceException;
import dev.pulceo.pna.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/internal/tasks")
public class InternalTaskController {

    private final Logger logger = LoggerFactory.getLogger(InternalTaskController.class);

    private final TaskService taskService;

    @Autowired
    public InternalTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // TODO: Update task by id
    // TODO: get rid of interrupted exception
    @PutMapping("/{id}")
    public ResponseEntity<UpdateTaskInternallyOnPNAResponseDTO> updateTaskById(@PathVariable String id, @Valid @RequestBody UpdateTaskInternallyOnPNADTO updateTaskInternallyOnPNADTO) throws TaskServiceException, InterruptedException {
        this.taskService.updateTaskInternally(id, updateTaskInternallyOnPNADTO.getNewTaskStatus(), updateTaskInternallyOnPNADTO.getProgress(), updateTaskInternallyOnPNADTO.getComment());
        return ResponseEntity.accepted().build();
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
