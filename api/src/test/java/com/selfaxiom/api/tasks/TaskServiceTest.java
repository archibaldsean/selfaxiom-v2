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
    TaskRequest request = new TaskRequest();
    request.setTask("Ship API");
    request.setDescription("Finish endpoints and docs");
    request.setFinishDate(LocalDate.now().plusDays(2));

    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(1L, 99L, request));
  }

  @Test
  void createTaskReturnsTaskResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);

    TaskRequest request = new TaskRequest();
    request.setTask("Write tests");
    request.setDescription("Cover services and controllers");
    request.setFinishDate(LocalDate.now().plusDays(3));

    Task savedTask = new Task(7L, goal, "Write tests", request.getDescription(), request.getFinishDate(), false);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
    when(taskRepository.countByGoal_Id(5L)).thenReturn(1L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(0L);

    TaskResponse response = taskService.createTask(1L, 5L, request);

    assertEquals(7L, response.getId());
    assertEquals(5L, response.getGoalId());
    assertEquals("Write tests", response.getTask());
    assertEquals("Cover services and controllers", response.getDescription());
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

    assertEquals(7L, response.getId());
    assertEquals(5L, response.getGoalId());
    assertEquals("Write tests", response.getTask());
    assertEquals("Cover services", response.getDescription());
  }

  @Test
  void updateThrowsWhenGoalMissing() {
    TaskUpdateRequest request = new TaskUpdateRequest();
    request.setTask("Updated task");
    request.setDescription("Updated details");
    request.setFinishDate(LocalDate.now().plusDays(5));
    request.setCompleted(true);

    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, 99L, 7L, request));
  }

  @Test
  void updateThrowsWhenTaskMissingWithinGoal() {
    TaskUpdateRequest request = new TaskUpdateRequest();
    request.setTask("Updated task");
    request.setDescription("Updated details");
    request.setFinishDate(LocalDate.now().plusDays(5));
    request.setCompleted(true);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(new Goal()));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, 5L, 7L, request));
  }

  @Test
  void updateReturnsUpdatedTaskResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), false);
    Task existing = new Task(7L, goal, "Old task", "Old details", LocalDate.now().plusDays(3), false);

    TaskUpdateRequest request = new TaskUpdateRequest();
    request.setTask("Updated task");
    request.setDescription("Updated details");
    request.setFinishDate(LocalDate.now().plusDays(8));
    request.setCompleted(true);

    Task saved = new Task(7L, goal, request.getTask(), request.getDescription(), request.getFinishDate(), true);

    when(goalRepository.findByIdAndUser_Id(5L, 1L)).thenReturn(Optional.of(goal));
    when(taskRepository.findByIdAndGoal_Id(7L, 5L)).thenReturn(Optional.of(existing));
    when(taskRepository.save(any(Task.class))).thenReturn(saved);
    when(taskRepository.countByGoal_Id(5L)).thenReturn(3L);
    when(taskRepository.countByGoal_IdAndCompletedTrue(5L)).thenReturn(3L);

    TaskResponse response = taskService.update(1L, 5L, 7L, request);

    assertEquals(7L, response.getId());
    assertEquals(5L, response.getGoalId());
    assertEquals("Updated task", response.getTask());
    assertEquals("Updated details", response.getDescription());
    assertEquals(true, response.isCompleted());
    verify(rewardService).applyTaskCompletionChange(saved, false, true);
    verify(rewardService).applyGoalCompletionChange(goal, false, true);
  }

  @Test
  void updateReopenRemovesTaskPoints() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 110);
    Goal goal = new Goal(5L, user, "Launch", "Ship milestone", LocalDate.now().plusDays(30), true);
    Task existing = new Task(7L, goal, "Done task", "Old details", LocalDate.now().plusDays(3), true);

    TaskUpdateRequest request = new TaskUpdateRequest();
    request.setTask("Done task");
    request.setDescription("Old details");
    request.setFinishDate(LocalDate.now().plusDays(3));
    request.setCompleted(false);

    Task saved = new Task(7L, goal, request.getTask(), request.getDescription(), request.getFinishDate(), false);

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

    TaskUpdateRequest request = new TaskUpdateRequest();
    request.setTask("Updated task");
    request.setDescription("Updated details");
    request.setFinishDate(LocalDate.now().plusDays(8));
    request.setCompleted(true);

    Task saved = new Task(7L, goal, request.getTask(), request.getDescription(), request.getFinishDate(), true);

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
