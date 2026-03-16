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

  public GoalResponse createGoal(Long userId, GoalRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Goal goal = new Goal(null, user, request.getGoal(), request.getDescription(), request.getFinishDate(), false);
    Goal savedGoal = goalRepository.save(goal);
    return mapToResponse(savedGoal);
  }

  public List<GoalResponse> listByUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found");
    }
    return goalRepository.findByUser_Id(userId).stream().map(this::mapToResponse).toList();
  }

  public GoalResponse getById(Long userId, Long id) {
    Goal goal = goalRepository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    return mapToResponse(goal);
  }

  public GoalResponse update(Long userId, Long id, GoalUpdateRequest request) {
    Goal goal = goalRepository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    goal.setGoal(request.getGoal());
    goal.setDescription(request.getDescription());
    goal.setFinishDate(request.getFinishDate());

    Goal savedGoal = goalRepository.save(goal);
    return mapToResponse(savedGoal);
  }

  public void delete(Long userId, Long id) {
    Goal goal = goalRepository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    goalRepository.delete(goal);
  }

  private GoalResponse mapToResponse(Goal goal) {
    return new GoalResponse(goal.getId(), goal.getUser().getId(), goal.getGoal(), goal.getDescription(),
        goal.getFinishDate(), goal.getCompleted());
  }

}
