package com.selfaxiom.api.goals;

import jakarta.validation.Valid;
import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
public class GoalController {

  private final GoalService goalService;

  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  @PostMapping
  public ResponseEntity<GoalResponse> createGoal(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @Valid @RequestBody GoalRequest request) {
    GoalResponse response = goalService.createGoal(currentUser.id(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<GoalResponse>> listGoalsByUser(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    return ResponseEntity.ok(goalService.listByUser(currentUser.id()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GoalResponse> getGoalById(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long id) {
    return ResponseEntity.ok(goalService.getById(currentUser.id(), id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<GoalResponse> updateGoal(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long id,
      @Valid @RequestBody GoalUpdateRequest request) {
    return ResponseEntity.ok(goalService.update(currentUser.id(), id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGoal(@AuthenticationPrincipal AuthenticatedUser currentUser, @PathVariable Long id) {
    goalService.delete(currentUser.id(), id);
    return ResponseEntity.noContent().build();
  }
}
