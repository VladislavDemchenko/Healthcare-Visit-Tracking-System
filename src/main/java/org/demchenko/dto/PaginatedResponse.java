package org.demchenko.dto;

import java.util.List;

public record PaginatedResponse(List<PatientResponse> data, Long count) {
}
