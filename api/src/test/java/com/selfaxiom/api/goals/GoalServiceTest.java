package com.selfaxiom.api.goals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    request.setUserId(99L);
    request.setGoal("Finish project");
    request.setFinishDate(LocalDate.now().plusDays(7));

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> goalService.createGoal(request));
  }

  @Test
  void createGoalReturnsGoalResponse() {
    User user = new User(1L, "archi", "archi@example.com", "hash");
    GoalRequest request = new GoalRequest();
    request.setUserId(1L);
    request.setGoal("Launch MVP");
    request.setFinishDate(LocalDate.now().plusDays(14));

    Goal saved = new Goal(10L, user, "Launch MVP", request.getFinishDate(), false);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(goalRepository.save(any(Goal.class))).thenReturn(saved);

    GoalResponse response = goalService.createGoal(request);

    assertEquals(10L, response.getId());
    assertEquals(1L, response.getUserId());
    assertEquals("Launch MVP", response.getGoal());
  }
}
