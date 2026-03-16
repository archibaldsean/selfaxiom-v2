package com.selfaxiom.api.tasks;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.goals.GoalRepository;
import com.selfaxiom.api.rewards.RewardService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final GoalRepository goalRepository;
  private final RewardService rewardService;

  public TaskService(TaskRepository taskRepository, GoalRepository goalRepository, RewardService rewardService) {
    this.taskRepository = taskRepository;
    this.goalRepository = goalRepository;
    this.rewardService = rewardService;
  }

  @Transactional
  public TaskResponse createTask(Long userId, Long goalId, TaskRequest request) {
    Goal goal = findOwnedGoalOrThrow(userId, goalId);

    Task task = new Task(null, goal, request.getTask(), request.getDescription(), request.getFinishDate(), false);
    Task savedTask = taskRepository.save(task);
    syncGoalCompletion(goal);
    return mapToResponse(savedTask);
  }

  public List<TaskResponse> listByGoal(Long userId, Long goalId) {
    findOwnedGoalOrThrow(userId, goalId);
    return taskRepository.findByGoal_Id(goalId).stream().map(this::mapToResponse).toList();
  }

  public TaskResponse getById(Long userId, Long goalId, Long taskId) {
    findOwnedGoalOrThrow(userId, goalId);

    Task task = taskRepository.findByIdAndGoal_Id(taskId, goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    return mapToResponse(task);
  }

  @Transactional
  public TaskResponse update(Long userId, Long goalId, Long taskId, TaskUpdateRequest request) {
    findOwnedGoalOrThrow(userId, goalId);

    Task task = taskRepository.findByIdAndGoal_Id(taskId, goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    boolean wasCompleted = task.getCompleted();
    task.setTask(request.getTask());
    task.setDescription(request.getDescription());
    task.setFinishDate(request.getFinishDate());
    task.setCompleted(request.getCompleted());

    Task savedTask = taskRepository.save(task);
    rewardService.applyTaskCompletionChange(savedTask, wasCompleted, savedTask.getCompleted());
    syncGoalCompletion(task.getGoal());
    return mapToResponse(savedTask);
  }

  @Transactional
  public void delete(Long userId, Long goalId, Long taskId) {
    findOwnedGoalOrThrow(userId, goalId);

    Task task = taskRepository.findByIdAndGoal_Id(taskId, goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    taskRepository.delete(task);
    syncGoalCompletion(task.getGoal());
  }

  private TaskResponse mapToResponse(Task task) {
    return new TaskResponse(task.getId(), task.getGoal().getId(), task.getTask(), task.getDescription(),
        task.getFinishDate(),
        task.getCompleted());
  }

  private void syncGoalCompletion(Goal goal) {
    boolean wasCompleted = goal.getCompleted();
    long totalTasks = taskRepository.countByGoal_Id(goal.getId());
    long completedTasks = taskRepository.countByGoal_IdAndCompletedTrue(goal.getId());
    boolean shouldBeCompleted = totalTasks > 0 && totalTasks == completedTasks;

    if (wasCompleted != shouldBeCompleted) {
      goal.setCompleted(shouldBeCompleted);
      goalRepository.save(goal);
      rewardService.applyGoalCompletionChange(goal, wasCompleted, shouldBeCompleted);
    }
  }

  private Goal findOwnedGoalOrThrow(Long userId, Long goalId) {
    return goalRepository.findByIdAndUser_Id(goalId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
  }
}
