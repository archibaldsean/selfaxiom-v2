package com.selfaxiom.api.tasks;

import java.time.LocalDate;

public class TaskResponse {

  private final Long id;
  private final Long goalId;
  private final String task;
  private final LocalDate finishDate;
  private final boolean completed;

  public TaskResponse(Long id, Long goalId, String task, LocalDate finishDate, boolean completed) {
    this.id = id;
    this.goalId = goalId;
    this.task = task;
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

  public LocalDate getFinishDate() {
    return finishDate;
  }

  public boolean isCompleted() {
    return completed;
  }
}
