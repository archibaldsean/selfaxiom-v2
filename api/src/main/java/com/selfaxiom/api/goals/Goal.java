package com.selfaxiom.api.goals;

import com.selfaxiom.api.user.User;
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
@Table(name = "goals", schema = "selfaxiom")
public class Goal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  @Column(name = "goal", nullable = false)
  private String goal;
  @Column(name = "description")
  private String description;
  @Column(name = "finish_date", nullable = false)
  private LocalDate finishDate;
  @Column(name = "completed")
  private boolean completed;

  public Goal() {
  }

  // Constructor
  public Goal(Long id, User user, String goal, String description, LocalDate finishDate, boolean completed) {
    this.id = id;
    this.user = user;
    this.goal = goal;
    this.description = description;
    this.finishDate = finishDate;
    this.completed = completed;
  }

  public Goal(User user, String goal, String description, LocalDate finishDate, boolean completed) {
    this.user = user;
    this.goal = goal;
    this.description = description;
    this.finishDate = finishDate;
    this.completed = completed;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
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

  public boolean getCompleted() {
    return completed;
  }

  // Setters
  public void setGoal(String goal) {
    this.goal = goal;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFinishDate(LocalDate finishDate) {
    this.finishDate = finishDate;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Goal that = (Goal) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

}
