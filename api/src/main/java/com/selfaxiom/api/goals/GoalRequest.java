package com.selfaxiom.api.goals;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GoalRequest(
    @NotBlank String goal,
    String description,
    @NotNull LocalDate finishDate) {
}
