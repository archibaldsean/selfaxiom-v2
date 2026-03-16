package com.selfaxiom.api.goals;

import jakarta.validation.Valid;
import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  @ResponseStatus(HttpStatus.CREATED)
  public GoalResponse createGoal(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @Valid @RequestBody GoalRequest request) {
    return goalService.createGoal(currentUser.id(), request);
  }

  @GetMapping
  public List<GoalResponse> listGoalsByUser(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    return goalService.listByUser(currentUser.id());
  }

  @GetMapping("/{id}")
  public GoalResponse getGoalById(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long id) {
    return goalService.getById(currentUser.id(), id);
  }

  @PutMapping("/{id}")
  public GoalResponse updateGoal(
      @AuthenticationPrincipal AuthenticatedUser currentUser,
      @PathVariable Long id,
      @Valid @RequestBody GoalRequest request) {
    return goalService.update(currentUser.id(), id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGoal(@AuthenticationPrincipal AuthenticatedUser currentUser, @PathVariable Long id) {
    goalService.delete(currentUser.id(), id);
  }
}
