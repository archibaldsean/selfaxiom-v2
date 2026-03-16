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
    GoalRequest request = new GoalRequest("Finish project", "Close all open tracks", LocalDate.now().plusDays(7));

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.createGoal(99L, request));
  }

  @Test
  void createGoalReturnsGoalResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    GoalRequest request = new GoalRequest("Launch MVP", "Ship first public version", LocalDate.now().plusDays(14));

    Goal saved = new Goal(10L, user, "Launch MVP", request.description(), request.finishDate(), false);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(goalRepository.save(any(Goal.class))).thenReturn(saved);

    GoalResponse response = goalService.createGoal(1L, request);

    assertEquals(10L, response.id());
    assertEquals(1L, response.userId());
    assertEquals("Launch MVP", response.goal());
    assertEquals("Ship first public version", response.description());
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

    assertEquals(10L, response.id());
    assertEquals(1L, response.userId());
    assertEquals("Launch MVP", response.goal());
    assertEquals("Ship first public version", response.description());
  }

  @Test
  void updateThrowsWhenGoalMissing() {
    GoalRequest request = new GoalRequest("Updated goal", "Refined scope and milestones", LocalDate.now().plusDays(30));

    when(goalRepository.findByIdAndUser_Id(77L, 1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.update(1L, 77L, request));
  }

  @Test
  void updateReturnsUpdatedGoalResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash", 0);
    Goal existing = new Goal(10L, user, "Old goal", "Old details", LocalDate.now().plusDays(10), false);

    GoalRequest request = new GoalRequest("Updated goal", "Refined scope and milestones", LocalDate.now().plusDays(20));

    Goal saved = new Goal(10L, user, request.goal(), request.description(), request.finishDate(), false);

    when(goalRepository.findByIdAndUser_Id(10L, 1L)).thenReturn(Optional.of(existing));
    when(goalRepository.save(any(Goal.class))).thenReturn(saved);

    GoalResponse response = goalService.update(1L, 10L, request);

    assertEquals(10L, response.id());
    assertEquals("Updated goal", response.goal());
    assertEquals("Refined scope and milestones", response.description());
    assertEquals(false, response.completed());
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
