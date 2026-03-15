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

  public TaskResponse createTask(Long goalId, TaskRequest request) {
    Goal goal = goalRepository.findById(goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    Task task = new Task(null, goal, request.getTask(), request.getFinishDate(), false);
    Task savedTask = taskRepository.save(task);
    return mapToResponse(savedTask);
  }

  public List<TaskResponse> listByGoal(Long goalId) {
    if (!goalRepository.existsById(goalId)) {
      throw new ResourceNotFoundException("Goal not found");
    }
    return taskRepository.findByGoal_Id(goalId).stream().map(this::mapToResponse).toList();
  }

  private TaskResponse mapToResponse(Task task) {
    return new TaskResponse(task.getId(), task.getGoal().getId(), task.getTask(), task.getFinishDate(),
        task.getCompleted());
  }
}
