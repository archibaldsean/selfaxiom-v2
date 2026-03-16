package com.selfaxiom.api.rewards;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RewardEventRepository extends JpaRepository<RewardEvent, Long> {

  List<RewardEvent> findByUser_IdAndPointsDeltaGreaterThanOrderByCreatedAtDesc(Long userId, int minPointsDelta,
      Pageable pageable);

  boolean existsByUser_IdAndTask_IdAndEventType(Long userId, Long taskId, RewardEventType eventType);

  boolean existsByUser_IdAndGoal_IdAndEventType(Long userId, Long goalId, RewardEventType eventType);

  @Query("""
      select
      coalesce(sum(case when e.pointsDelta > 0 then e.pointsDelta else 0 end), 0) as totalEarned
      from RewardEvent e
      where e.user.id = :userId
      """)
  RewardTotals summarizeByUserId(@Param("userId") Long userId);
}
