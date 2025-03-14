package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.task.CreateNewTaskOnPnaDTO;
import dev.pulceo.pna.dto.task.CreateNewTaskOnPnaResponseDTO;
import dev.pulceo.pna.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity<CreateNewTaskOnPnaResponseDTO> createNewTaskOnPna(CreateNewTaskOnPnaDTO createNewTaskOnPnaDTO) {
        return ResponseEntity.status(200).body(CreateNewTaskOnPnaResponseDTO.builder().build());
    }

}
