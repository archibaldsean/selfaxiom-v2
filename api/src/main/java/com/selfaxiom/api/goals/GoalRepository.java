package com.selfaxiom.api.goals;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
  List<Goal> findByUser_Id(Long userId);
}
