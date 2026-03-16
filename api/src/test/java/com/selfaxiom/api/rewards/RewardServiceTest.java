package com.selfaxiom.api.rewards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.tasks.Task;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

  @Mock
  private RewardEventRepository rewardEventRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private RewardService rewardService;

  @Test
  void applyTaskCompletionChangeAddsPointsAndEvent() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 20);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), true);

    when(rewardEventRepository.existsByUser_IdAndTask_IdAndEventType(1L, 7L, RewardEventType.TASK_COMPLETED))
        .thenReturn(false);
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    rewardService.applyTaskCompletionChange(task, false, true);

    assertEquals(30, user.getPointsBalance());
    verify(userRepository).save(user);

    ArgumentCaptor<RewardEvent> eventCaptor = ArgumentCaptor.forClass(RewardEvent.class);
    verify(rewardEventRepository).saveAndFlush(eventCaptor.capture());
    assertEquals(RewardEventType.TASK_COMPLETED, eventCaptor.getValue().getEventType());
    assertEquals(10, eventCaptor.getValue().getPointsDelta());
    assertEquals(30, eventCaptor.getValue().getBalanceAfter());
  }

  @Test
  void applyGoalCompletionChangeAwardsPointsAndEvent() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 80);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);

    when(rewardEventRepository.existsByUser_IdAndGoal_IdAndEventType(1L, 5L, RewardEventType.GOAL_COMPLETED))
        .thenReturn(false);
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    rewardService.applyGoalCompletionChange(goal, false, true);

    assertEquals(180, user.getPointsBalance());
    verify(userRepository).save(user);

    ArgumentCaptor<RewardEvent> eventCaptor = ArgumentCaptor.forClass(RewardEvent.class);
    verify(rewardEventRepository).saveAndFlush(eventCaptor.capture());
    assertEquals(RewardEventType.GOAL_COMPLETED, eventCaptor.getValue().getEventType());
    assertEquals(100, eventCaptor.getValue().getPointsDelta());
    assertEquals(180, eventCaptor.getValue().getBalanceAfter());
  }

  @Test
  void applyGoalCompletionChangeSkipsWhenReopened() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 180);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);

    rewardService.applyGoalCompletionChange(goal, true, false);

    assertEquals(180, user.getPointsBalance());
    verify(userRepository, never()).save(user);
    verify(rewardEventRepository, never()).save(org.mockito.ArgumentMatchers.any(RewardEvent.class));
  }

  @Test
  void applyTaskCompletionChangeSkipsWhenTaskWasAwardedBefore() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 20);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), true);

    when(rewardEventRepository.existsByUser_IdAndTask_IdAndEventType(1L, 7L, RewardEventType.TASK_COMPLETED))
        .thenReturn(true);
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    rewardService.applyTaskCompletionChange(task, false, true);

    assertEquals(20, user.getPointsBalance());
    verify(userRepository, never()).save(user);
    verify(rewardEventRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any(RewardEvent.class));
  }

  @Test
  void applyGoalCompletionChangeSkipsWhenGoalWasAwardedBefore() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 80);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);

    when(rewardEventRepository.existsByUser_IdAndGoal_IdAndEventType(1L, 5L, RewardEventType.GOAL_COMPLETED))
        .thenReturn(true);
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    rewardService.applyGoalCompletionChange(goal, false, true);

    assertEquals(80, user.getPointsBalance());
    verify(userRepository, never()).save(user);
    verify(rewardEventRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any(RewardEvent.class));
  }

  @Test
  void applyTaskCompletionChangeSkipsWhenReopened() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 20);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), false);

    rewardService.applyTaskCompletionChange(task, true, false);

    assertEquals(20, user.getPointsBalance());
    verify(userRepository, never()).save(user);
    verify(rewardEventRepository, never()).save(org.mockito.ArgumentMatchers.any(RewardEvent.class));
  }

  @Test
  void summaryReturnsBalanceAndEarnedTotal() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 55);
    RewardTotals totals = new RewardTotals() {
      @Override
      public Long getTotalEarned() {
        return 210L;
      }
    };

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(rewardEventRepository.summarizeByUserId(1L)).thenReturn(totals);

    RewardSummaryResponse summary = rewardService.summary(1L);

    assertEquals(55, summary.getPointsBalance());
    assertEquals(210L, summary.getTotalEarned());
  }

  @Test
  void applyDeltaNeverDropsBelowZeroWhenNegativeDeltasExist() {
    User user = new User(1L, "archi", "archi@example.com", "hash", -15);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), true);

    when(rewardEventRepository.existsByUser_IdAndTask_IdAndEventType(1L, 7L, RewardEventType.TASK_COMPLETED))
        .thenReturn(false);
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));

    rewardService.applyTaskCompletionChange(task, false, true);

    ArgumentCaptor<RewardEvent> eventCaptor = ArgumentCaptor.forClass(RewardEvent.class);
    verify(rewardEventRepository).saveAndFlush(eventCaptor.capture());
    assertEquals(0, eventCaptor.getValue().getBalanceAfter());
    assertEquals(0, user.getPointsBalance());
  }

  @Test
  void historyMapsLatestEvents() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 10);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), true);
    RewardEvent event = new RewardEvent(null, user, goal, task, RewardEventType.TASK_COMPLETED, 10, 10, null);

    when(rewardEventRepository.findByUser_IdAndPointsDeltaGreaterThanOrderByCreatedAtDesc(
        org.mockito.ArgumentMatchers.eq(1L),
        org.mockito.ArgumentMatchers.eq(0),
        org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(List.of(event));

    List<RewardEventResponse> history = rewardService.history(1L);
    assertEquals(1, history.size());
    assertEquals(RewardEventType.TASK_COMPLETED, history.get(0).getEventType());
    assertEquals(10, history.get(0).getPointsDelta());
    assertEquals(5L, history.get(0).getGoalId());
    assertEquals(7L, history.get(0).getTaskId());
  }
}
