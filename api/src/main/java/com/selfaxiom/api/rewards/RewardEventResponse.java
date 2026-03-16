package com.selfaxiom.api.rewards;

import java.time.OffsetDateTime;

public class RewardEventResponse {

  private final Long id;
  private final RewardEventType eventType;
  private final int pointsDelta;
  private final int balanceAfter;
  private final Long goalId;
  private final Long taskId;
  private final OffsetDateTime createdAt;

  public RewardEventResponse(Long id, RewardEventType eventType, int pointsDelta, int balanceAfter, Long goalId,
      Long taskId, OffsetDateTime createdAt) {
    this.id = id;
    this.eventType = eventType;
    this.pointsDelta = pointsDelta;
    this.balanceAfter = balanceAfter;
    this.goalId = goalId;
    this.taskId = taskId;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public RewardEventType getEventType() {
    return eventType;
  }

  public int getPointsDelta() {
    return pointsDelta;
  }

  public int getBalanceAfter() {
    return balanceAfter;
  }

  public Long getGoalId() {
    return goalId;
  }

  public Long getTaskId() {
    return taskId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
