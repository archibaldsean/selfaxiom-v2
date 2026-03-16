package com.selfaxiom.api.rewards;

import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.tasks.Task;
import com.selfaxiom.api.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reward_events", schema = "selfaxiom")
public class RewardEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "goal_id")
  private Goal goal;

  @ManyToOne
  @JoinColumn(name = "task_id")
  private Task task;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private RewardEventType eventType;

  @Column(name = "points_delta", nullable = false)
  private int pointsDelta;

  @Column(name = "balance_after", nullable = false)
  private int balanceAfter;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public RewardEvent() {
  }

  public RewardEvent(
      Long id,
      User user,
      Goal goal,
      Task task,
      RewardEventType eventType,
      int pointsDelta,
      int balanceAfter,
      OffsetDateTime createdAt) {
    this.id = id;
    this.user = user;
    this.goal = goal;
    this.task = task;
    this.eventType = eventType;
    this.pointsDelta = pointsDelta;
    this.balanceAfter = balanceAfter;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public Goal getGoal() {
    return goal;
  }

  public Task getTask() {
    return task;
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

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public void setEventType(RewardEventType eventType) {
    this.eventType = eventType;
  }

  public void setPointsDelta(int pointsDelta) {
    this.pointsDelta = pointsDelta;
  }

  public void setBalanceAfter(int balanceAfter) {
    this.balanceAfter = balanceAfter;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
