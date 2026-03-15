package com.selfaxiom.api.goals;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
    GoalResponse response = goalService.createGoal(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<GoalResponse>> listGoalsByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(goalService.listByUser(userId));
  }
}
