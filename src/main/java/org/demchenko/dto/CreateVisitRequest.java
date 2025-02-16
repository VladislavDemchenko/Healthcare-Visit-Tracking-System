package org.demchenko.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateVisitRequest(
         @NotNull
         @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
         String start,
         @NotNull
         @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
         String end,
         @NotNull
         Long patientId,
         @NotNull
         Long doctorId) {
    public CreateVisitRequest {
        if (start != null && end != null &&
                LocalDateTime.parse(start).isAfter(LocalDateTime.parse(end))) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
