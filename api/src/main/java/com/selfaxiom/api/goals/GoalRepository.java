package com.selfaxiom.api.goals;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
  List<Goal> findByUser_Id(Long userId);

  Optional<Goal> findByIdAndUser_Id(Long id, Long userId);
}
