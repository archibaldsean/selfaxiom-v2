package com.selfaxiom.api.rewards;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.tasks.Task;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RewardService {

  public static final int TASK_COMPLETION_POINTS = 10;
  public static final int GOAL_COMPLETION_POINTS = 100;
  private static final int DEFAULT_HISTORY_LIMIT = 30;

  private final RewardEventRepository rewardEventRepository;
  private final UserRepository userRepository;

  public RewardService(RewardEventRepository rewardEventRepository, UserRepository userRepository) {
    this.rewardEventRepository = rewardEventRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void applyTaskCompletionChange(Task task, boolean wasCompleted, boolean isCompleted) {
    if (wasCompleted == isCompleted) {
      return;
    }

    if (!isCompleted) {
      return;
    }

    Long userId = task.getGoal().getUser().getId();
    Long taskId = task.getId();
    User user = findUserForUpdate(userId);
    boolean alreadyAwarded = rewardEventRepository.existsByUser_IdAndTask_IdAndEventType(
        userId,
        taskId,
        RewardEventType.TASK_COMPLETED);
    if (alreadyAwarded) {
      return;
    }

    applyDelta(user, task.getGoal(), task, RewardEventType.TASK_COMPLETED, TASK_COMPLETION_POINTS);
  }

  @Transactional
  public void applyGoalCompletionChange(Goal goal, boolean wasCompleted, boolean isCompleted) {
    if (wasCompleted == isCompleted) {
      return;
    }

    if (!isCompleted) {
      return;
    }

    Long userId = goal.getUser().getId();
    Long goalId = goal.getId();
    User user = findUserForUpdate(userId);
    boolean alreadyAwarded = rewardEventRepository.existsByUser_IdAndGoal_IdAndEventType(
        userId,
        goalId,
        RewardEventType.GOAL_COMPLETED);
    if (alreadyAwarded) {
      return;
    }

    applyDelta(user, goal, null, RewardEventType.GOAL_COMPLETED, GOAL_COMPLETION_POINTS);
  }

  public RewardSummaryResponse summary(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    RewardTotals totals = rewardEventRepository.summarizeByUserId(userId);

    long totalEarned = totals == null || totals.getTotalEarned() == null ? 0 : totals.getTotalEarned();
    return new RewardSummaryResponse(user.getPointsBalance(), totalEarned);
  }

  public List<RewardEventResponse> history(Long userId) {
    PageRequest request = PageRequest.of(0, DEFAULT_HISTORY_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt"));
    return rewardEventRepository.findByUser_IdAndPointsDeltaGreaterThanOrderByCreatedAtDesc(userId, 0, request)
        .stream()
        .map(this::mapToResponse)
        .toList();
  }

  private RewardEventResponse mapToResponse(RewardEvent event) {
    return new RewardEventResponse(
        event.getId(),
        event.getEventType(),
        event.getPointsDelta(),
        event.getBalanceAfter(),
        event.getGoal() == null ? null : event.getGoal().getId(),
        event.getTask() == null ? null : event.getTask().getId(),
        event.getCreatedAt());
  }

  private void applyDelta(User user, Goal goal, Task task, RewardEventType type, int pointsDelta) {
    int nextBalance = Math.max(0, user.getPointsBalance() + pointsDelta);

    RewardEvent event = new RewardEvent(
        null,
        user,
        goal,
        task,
        type,
        pointsDelta,
        nextBalance,
        OffsetDateTime.now());

    try {
      rewardEventRepository.saveAndFlush(event);
    } catch (DataIntegrityViolationException exception) {
      if (isDuplicateRewardEventViolation(exception)) {
        return;
      }
      throw exception;
    }

    user.setPointsBalance(nextBalance);
    userRepository.save(user);
  }

  private User findUserForUpdate(Long userId) {
    return userRepository.findByIdForUpdate(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private boolean isDuplicateRewardEventViolation(DataIntegrityViolationException exception) {
    Throwable cursor = exception;
    while (cursor != null) {
      String message = cursor.getMessage();
      if (message != null
          && (message.contains("ux_reward_events_task_event_once")
              || message.contains("ux_reward_events_goal_event_once"))) {
        return true;
      }
      cursor = cursor.getCause();
    }
    return false;
  }
}
