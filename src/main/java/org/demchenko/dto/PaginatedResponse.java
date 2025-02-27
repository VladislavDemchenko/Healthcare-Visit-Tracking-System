package org.demchenko.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PaginatedResponse implements Serializable {
        private static final long serialVersionUID = 1L;

        private List<PatientResponse> data;
        private int count;
}
