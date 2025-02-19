package org.demchenko.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.demchenko.exception.BadRequestException;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CreateVisitRequest implements Serializable{

         @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")
         private String start;

         @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?")
         private String end;
         private Long patientId;
         private Long doctorId;

    public CreateVisitRequest(String start, String end, Long patientId, Long doctorId) {
        this.start = start;
        this.end = end;
        this.patientId = patientId;
        this.doctorId = doctorId;

        if (start == null || end == null || start.isEmpty() || end.isEmpty() || start.equals(end) ||
                LocalDateTime.parse(start).isAfter(LocalDateTime.parse(end))) {
            throw new BadRequestException("Start date must be before end date");
        }
    }
}
