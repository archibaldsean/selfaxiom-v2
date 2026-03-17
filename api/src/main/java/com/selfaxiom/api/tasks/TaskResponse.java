package com.selfaxiom.api.tasks;

import java.time.LocalDate;

public record TaskResponse(
    Long id,
    Long goalId,
    String task,
    String description,
    LocalDate finishDate,
    boolean completed) {
}
