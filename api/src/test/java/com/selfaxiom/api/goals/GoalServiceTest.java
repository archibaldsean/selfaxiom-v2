package com.selfaxiom.api.goals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

  @Mock
  private GoalRepository goalRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private GoalService goalService;

  @Test
  void createGoalThrowsWhenUserMissing() {
    GoalRequest request = new GoalRequest();
    request.setGoal("Finish project");
    request.setDescription("Close all open tracks");
    request.setFinishDate(LocalDate.now().plusDays(7));

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.createGoal(99L, request));
  }

  @Test
  void createGoalReturnsGoalResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    GoalRequest request = new GoalRequest();
    request.setGoal("Launch MVP");
    request.setDescription("Ship first public version");
    request.setFinishDate(LocalDate.now().plusDays(14));

    Goal saved = new Goal(10L, user, "Launch MVP", request.getDescription(), request.getFinishDate(), false);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(goalRepository.save(any(Goal.class))).thenReturn(saved);

    GoalResponse response = goalService.createGoal(1L, request);

    assertEquals(10L, response.getId());
    assertEquals(1L, response.getUserId());
    assertEquals("Launch MVP", response.getGoal());
    assertEquals("Ship first public version", response.getDescription());
  }

  @Test
  void getByIdThrowsWhenGoalMissing() {
    when(goalRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.getById(1L, 99L));
  }

  @Test
  void getByIdReturnsGoalResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(10L, user, "Launch MVP", "Ship first public version", LocalDate.now().plusDays(14), false);
    when(goalRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.of(goal));

    GoalResponse response = goalService.getById(1L, 10L);

    assertEquals(10L, response.getId());
    assertEquals(1L, response.getUserId());
    assertEquals("Launch MVP", response.getGoal());
    assertEquals("Ship first public version", response.getDescription());
  }

  @Test
  void updateThrowsWhenGoalMissing() {
    GoalUpdateRequest request = new GoalUpdateRequest();
    request.setGoal("Updated goal");
    request.setDescription("Refined scope and milestones");
    request.setFinishDate(LocalDate.now().plusDays(30));

    when(goalRepository.findByIdAndUser_Id(77L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.update(1L, 77L, request));
  }

  @Test
  void updateReturnsUpdatedGoalResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal existing = new Goal(10L, user, "Old goal", "Old details", LocalDate.now().plusDays(10), false);

    GoalUpdateRequest request = new GoalUpdateRequest();
    request.setGoal("Updated goal");
    request.setDescription("Refined scope and milestones");
    request.setFinishDate(LocalDate.now().plusDays(20));

    Goal saved = new Goal(10L, user, request.getGoal(), request.getDescription(), request.getFinishDate(), false);

    when(goalRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.of(existing));
    when(goalRepository.save(any(Goal.class))).thenReturn(saved);

    GoalResponse response = goalService.update(1L, 10L, request);

    assertEquals(10L, response.getId());
    assertEquals("Updated goal", response.getGoal());
    assertEquals("Refined scope and milestones", response.getDescription());
    assertEquals(false, response.isCompleted());
  }

  @Test
  void deleteThrowsWhenGoalMissing() {
    when(goalRepository.findByIdAndUser_Id(45L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.delete(1L, 45L));
  }

  @Test
  void deleteRemovesGoalWhenFound() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal goal = new Goal(10L, user, "Launch MVP", "Ship first public version", LocalDate.now().plusDays(14), false);
    when(goalRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.of(goal));

    goalService.delete(1L, 10L);

    verify(goalRepository).delete(goal);
  }
}
