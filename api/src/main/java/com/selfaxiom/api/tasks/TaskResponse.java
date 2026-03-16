package com.selfaxiom.api.tasks;

import java.time.LocalDate;

public class TaskResponse {

  private final Long id;
  private final Long goalId;
  private final String task;
  private final String description;
  private final LocalDate finishDate;
  private final boolean completed;

  public TaskResponse(Long id, Long goalId, String task, String description, LocalDate finishDate,
      boolean completed) {
    this.id = id;
    this.goalId = goalId;
    this.task = task;
    this.description = description;
    this.finishDate = finishDate;
    this.completed = completed;
  }

  public Long getId() {
    return id;
  }

  public Long getGoalId() {
    return goalId;
  }

  public String getTask() {
    return task;
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
