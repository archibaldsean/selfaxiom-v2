package com.selfaxiom.api.goals;

import java.time.LocalDate;

public class GoalResponse {

  private final Long id;
  private final Long userId;
  private final String goal;
  private final String description;
  private final LocalDate finishDate;
  private final boolean completed;

  public GoalResponse(Long id, Long userId, String goal, String description, LocalDate finishDate,
      boolean completed) {
    this.id = id;
    this.userId = userId;
    this.goal = goal;
    this.description = description;
    this.finishDate = finishDate;
    this.completed = completed;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getGoal() {
    return goal;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getFinishDate() {
    return finishDate;
  }

  public boolean isCompleted() {
    return completed;
  }
}
