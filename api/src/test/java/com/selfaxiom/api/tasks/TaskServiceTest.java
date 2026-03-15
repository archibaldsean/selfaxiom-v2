package com.selfaxiom.api.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.goals.GoalRepository;
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

  @InjectMocks
  private TaskService taskService;

  @Test
  void createTaskThrowsWhenGoalMissing() {
    TaskRequest request = new TaskRequest();
    request.setTask("Ship API");
    request.setFinishDate(LocalDate.now().plusDays(2));

    when(goalRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(99L, request));
  }

  @Test
  void createTaskReturnsTaskResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash");
    Goal goal = new Goal(5L, user, "Launch", LocalDate.now().plusDays(30), false);

    TaskRequest request = new TaskRequest();
    request.setTask("Write tests");
    request.setFinishDate(LocalDate.now().plusDays(3));

    Task savedTask = new Task(7L, goal, "Write tests", request.getFinishDate(), false);

    when(goalRepository.findById(5L)).thenReturn(Optional.of(goal));
    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    TaskResponse response = taskService.createTask(5L, request);

    assertEquals(7L, response.getId());
    assertEquals(5L, response.getGoalId());
    assertEquals("Write tests", response.getTask());
  }
}
