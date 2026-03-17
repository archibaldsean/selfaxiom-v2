package com.selfaxiom.api.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.goals.GoalRepository;
import com.selfaxiom.api.rewards.RewardService;
import com.selfaxiom.api.user.User;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private GoalRepository goalRepository;

  @Mock
  private RewardService rewardService;

  @InjectMocks
  private TaskService taskService;

  @Test
  void createTaskThrowsWhenGoalMissing() {
    TaskRequest request = new TaskRequest("Ship API", "Finish endpoints and docs", LocalDate.now().plusDays(2));

    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(1L, 99L, request));
  }

  @Test
  void createTaskReturnsTaskResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);

    TaskRequest request = new TaskRequest("Write tests", "Cover services and controllers", LocalDate.now().plusDays(3));

    Task savedTask = new Task(7L, goal, "Write tests", request.description(), request.finishDate(), false);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
    when(taskRepository.countByGoal_Id(5L)).thenReturn(1L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(0L);

    TaskResponse response = taskService.createTask(1L, 5L, request);

    assertEquals(7L, response.id());
    assertEquals(5L, response.goalId());
    assertEquals("Write tests", response.task());
    assertEquals("Cover services and controllers", response.description());
  }

  @Test
  void getByIdThrowsWhenGoalMissing() {
    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.getById(1L, 99L, 7L));
  }

  @Test
  void getByIdThrowsWhenTaskMissingWithinGoal() {
    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(new Goal()));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.getById(1L, 5L, 7L));
  }

  @Test
  void getByIdReturnsTaskResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), false);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(task));

    TaskResponse response = taskService.getById(1L, 5L, 7L);

    assertEquals(7L, response.id());
    assertEquals(5L, response.goalId());
    assertEquals("Write tests", response.task());
    assertEquals("Cover services", response.description());
  }

  @Test
  void updateThrowsWhenGoalMissing() {
    TaskUpdateRequest request = new TaskUpdateRequest("Updated task", "Updated details", LocalDate.now().plusDays(5), true);

    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, 99L, 7L, request));
  }

  @Test
  void updateThrowsWhenTaskMissingWithinGoal() {
    TaskUpdateRequest request = new TaskUpdateRequest("Updated task", "Updated details", LocalDate.now().plusDays(5), true);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(new Goal()));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, 5L, 7L, request));
  }

  @Test
  void updateReturnsUpdatedTaskResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task existing = new Task(7L, goal, "Old task", "Old details", LocalDate.now().plusDays(3), false);

    TaskUpdateRequest request = new TaskUpdateRequest("Updated task", "Updated details", LocalDate.now().plusDays(8), true);

    Task saved = new Task(7L, goal, request.task(), request.description(), request.finishDate(), true);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(existing));
    when(taskRepository.save(any(Task.class))).thenReturn(saved);
    when(taskRepository.countByGoal_Id(5L)).thenReturn(3L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(3L);

    TaskResponse response = taskService.update(1L, 5L, 7L, request);

    assertEquals(7L, response.id());
    assertEquals(5L, response.goalId());
    assertEquals("Updated task", response.task());
    assertEquals("Updated details", response.description());
    assertEquals(true, response.completed());
    verify(rewardService).applyTaskCompletionChange(saved, false, true);
    verify(rewardService).applyGoalCompletionChange(goal, false, true);
  }

  @Test
  void updateReopenRemovesTaskPoints() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 110);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), true);
    Task existing = new Task(7L, goal, "Done task", "Old details", LocalDate.now().plusDays(3), true);

    TaskUpdateRequest request = new TaskUpdateRequest("Done task", "Old details", LocalDate.now().plusDays(3), false);

    Task saved = new Task(7L, goal, request.task(), request.description(), request.finishDate(), false);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(existing));
    when(taskRepository.save(any(Task.class))).thenReturn(saved);
    when(taskRepository.countByGoal_Id(5L)).thenReturn(3L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(2L);

    taskService.update(1L, 5L, 7L, request);

    verify(rewardService).applyTaskCompletionChange(saved, true, false);
    verify(rewardService).applyGoalCompletionChange(goal, true, false);
  }

  @Test
  void deleteThrowsWhenGoalMissing() {
    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.delete(1L, 99L, 7L));
  }

  @Test
  void deleteThrowsWhenTaskMissingWithinGoal() {
    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(new Goal()));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.delete(1L, 5L, 7L));
  }

  @Test
  void deleteRemovesTaskWhenFound() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), false);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(task));
    when(taskRepository.countByGoal_Id(5L)).thenReturn(0L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(0L);

    taskService.delete(1L, 5L, 7L);

    verify(taskRepository).delete(task);
  }

  @Test
  void updateMarksGoalCompleteWhenAllTasksComplete() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task existing = new Task(7L, goal, "Old task", "Old details", LocalDate.now().plusDays(3), false);

    TaskUpdateRequest request = new TaskUpdateRequest("Updated task", "Updated details", LocalDate.now().plusDays(8), true);

    Task saved = new Task(7L, goal, request.task(), request.description(), request.finishDate(), true);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(existing));
    when(taskRepository.save(any(Task.class))).thenReturn(saved);
    when(taskRepository.countByGoal_Id(5L)).thenReturn(2L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(2L);

    taskService.update(1L, 5L, 7L, request);

    assertEquals(true, goal.getCompleted());
    verify(goalRepository).save(goal);
    verify(rewardService).applyGoalCompletionChange(goal, false, true);
  }

  @Test
  void deleteMarksGoalIncompleteWhenNoTasksRemain() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), true);
    Task task = new Task(7L, goal, "Write tests", "Cover services", LocalDate.now().plusDays(3), true);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(task));
    when(taskRepository.countByGoal_Id(5L)).thenReturn(0L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(0L);

    taskService.delete(1L, 5L, 7L);

    assertEquals(false, goal.getCompleted());
    verify(goalRepository).save(goal);
    verify(rewardService).applyGoalCompletionChange(goal, true, false);
  }
}
