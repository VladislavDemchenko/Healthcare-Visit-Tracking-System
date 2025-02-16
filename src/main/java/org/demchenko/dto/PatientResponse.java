package org.demchenko.dto;

import java.util.List;

public record PatientResponse(String firstName,
                              String lastName,
                              List<VisitDto> listVisits) {
}
