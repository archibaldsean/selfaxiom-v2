package com.selfaxiom.api.tasks;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskRequest(
    @NotBlank String task,
    String description,
    @NotNull LocalDate finishDate) {
}
