package com.selfaxiom.api.tasks;

import com.selfaxiom.api.goals.Goal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasks", schema = "selfaxiom")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(optional = false)
  @JoinColumn(name = "goal_id", nullable = false)
  private Goal goal;
  @Column(name = "task", nullable = false)
  private String task;
  @Column(name = "description")
  private String description;
  @Column(name = "finish_date", nullable = false)
  private LocalDate finishDate;
  @Column(name = "completed")
  private boolean completed;

  public Task() {
  }

  // Constructor
  public Task(Long id, Goal goal, String task, String description, LocalDate finishDate, boolean completed) {
    this.id = id;
    this.goal = goal;
    this.task = task;
    this.description = description;
    this.finishDate = finishDate;
    this.completed = completed;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public Goal getGoal() {
    return goal;
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

  public boolean getCompleted() {
    return completed;
  }

  // Setters
  public void setId(Long id) {
    this.id = id;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public void setTask(String task) {
    this.task = task;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFinishDate(LocalDate finishDate) {
    this.finishDate = finishDate;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Task that = (Task) o;
    return completed == that.completed && Objects.equals(id, that.id) && Objects.equals(goal, that.goal)
        && Objects.equals(task, that.task) && Objects.equals(description, that.description)
        && Objects.equals(finishDate, that.finishDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, goal, task, description, finishDate, completed);
  }

}
