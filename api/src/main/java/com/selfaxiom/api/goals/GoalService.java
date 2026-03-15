package com.selfaxiom.api.goals;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GoalService {

  private final GoalRepository goalRepository;
  private final UserRepository userRepository;

  public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
    this.goalRepository = goalRepository;
    this.userRepository = userRepository;
  }

  public GoalResponse createGoal(GoalRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Goal goal = new Goal(null, user, request.getGoal(), request.getFinishDate(), false);
    Goal savedGoal = goalRepository.save(goal);
    return mapToResponse(savedGoal);
  }

  public List<GoalResponse> listByUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found");
    }
    return goalRepository.findByUser_Id(userId).stream().map(this::mapToResponse).toList();
  }

  private GoalResponse mapToResponse(Goal goal) {
    return new GoalResponse(goal.getId(), goal.getUser().getId(), goal.getGoal(), goal.getFinishDate(),
        goal.getCompleted());
  }

}
