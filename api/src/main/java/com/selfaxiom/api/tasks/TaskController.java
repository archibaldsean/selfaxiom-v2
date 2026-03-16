package com.selfaxiom.api.tasks;

import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goals/{goalId}/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long goalId,
      @Valid @RequestBody TaskRequest request) {
    TaskResponse response = taskService.createTask(currentUser.id(), goalId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<TaskResponse>> listTasksByGoal(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long goalId) {
    return ResponseEntity.ok(taskService.listByGoal(currentUser.id(), goalId));
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<TaskResponse> getTaskById(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long goalId,
      @PathVariable Long taskId) {
    return ResponseEntity.ok(taskService.getById(currentUser.id(), goalId, taskId));
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<TaskResponse> updateTask(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long goalId,
      @PathVariable Long taskId,
      @Valid @RequestBody TaskUpdateRequest request) {
    return ResponseEntity.ok(taskService.update(currentUser.id(), goalId, taskId, request));
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> deleteTask(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long goalId,
      @PathVariable Long taskId) {
    taskService.delete(currentUser.id(), goalId, taskId);
    return ResponseEntity.noContent().build();
  }
}
