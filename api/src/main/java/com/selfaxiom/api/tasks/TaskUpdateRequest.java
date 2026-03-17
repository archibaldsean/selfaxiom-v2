package com.selfaxiom.api.tasks;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskUpdateRequest(
    @NotBlank String task,
    String description,
    @NotNull LocalDate finishDate,
    @NotNull Boolean completed) {
}
