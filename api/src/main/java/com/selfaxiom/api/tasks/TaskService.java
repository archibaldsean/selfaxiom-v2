package com.selfaxiom.api.tasks;

import com.selfaxiom.api.ResourceNotFoundException;
import com.selfaxiom.api.goals.Goal;
import com.selfaxiom.api.goals.GoalRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final GoalRepository goalRepository;

  public TaskService(TaskRepository taskRepository, GoalRepository goalRepository) {
    this.taskRepository = taskRepository;
    this.goalRepository = goalRepository;
  }

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

  public TaskResponse update(Long userId, Long goalId, Long taskId, TaskUpdateRequest request) {
    findOwnedGoalOrThrow(userId, goalId);

    Task task = taskRepository.findByIdAndGoal_Id(taskId, goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    task.setTask(request.getTask());
    task.setDescription(request.getDescription());
    task.setFinishDate(request.getFinishDate());
    task.setCompleted(request.getCompleted());

    Task savedTask = taskRepository.save(task);
    syncGoalCompletion(task.getGoal());
    return mapToResponse(savedTask);
  }

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
    long totalTasks = taskRepository.countByGoal_Id(goal.getId());
    long completedTasks = taskRepository.countByGoal_IdAndCompletedTrue(goal.getId());
    boolean shouldBeCompleted = totalTasks > 0 && totalTasks == completedTasks;

    if (goal.getCompleted() != shouldBeCompleted) {
      goal.setCompleted(shouldBeCompleted);
      goalRepository.save(goal);
    }
  }

  private Goal findOwnedGoalOrThrow(Long userId, Long goalId) {
    return goalRepository.findByIdAndUser_Id(goalId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
  }
}
