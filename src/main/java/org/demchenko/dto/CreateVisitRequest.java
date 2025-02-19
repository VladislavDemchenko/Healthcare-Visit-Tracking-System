package org.demchenko.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.demchenko.exception.BadRequestException;

import java.time.LocalDateTime;

public record CreateVisitRequest(
         @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")
         String start,
         @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")
         String end,
         Long patientId,
         Long doctorId) {
    public CreateVisitRequest {
        if (start == null || end == null || start.isEmpty() || end.isEmpty() || start.equals(end) ||
                LocalDateTime.parse(start).isAfter(LocalDateTime.parse(end))) {
            throw new BadRequestException("Start date must be before end date");
        }
    }
}
