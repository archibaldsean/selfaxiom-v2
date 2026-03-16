package com.selfaxiom.api.goals;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GoalService {

  private final GoalRepository goalRepository;
  private final UserRepository userRepository;

  public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
    this.goalRepository = goalRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public GoalResponse createGoal(Long userId, GoalRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Goal goal = new Goal(user, request.goal(), request.description(), request.finishDate(), false);
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

  @Transactional
  public GoalResponse update(Long userId, Long id, GoalRequest request) {
    Goal goal = goalRepository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    goal.setGoal(request.goal());
    goal.setDescription(request.description());
    goal.setFinishDate(request.finishDate());

    Goal savedGoal = goalRepository.save(goal);
    return mapToResponse(savedGoal);
  }

  @Transactional
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
