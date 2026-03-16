package com.selfaxiom.api.goals;

import java.time.LocalDate;

public record GoalResponse(
    Long id,
    Long userId,
    String goal,
    String description,
    LocalDate finishDate,
    boolean completed) {
}
