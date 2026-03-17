package com.selfaxiom.api.tasks;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByGoal_Id(Long goalId);

  Optional<Task> findByIdAndGoal_Id(Long taskId, Long goalId);

  long countByGoal_Id(Long goalId);

  long countByGoal_IdAndCompletedTrue(Long goalId);
}
