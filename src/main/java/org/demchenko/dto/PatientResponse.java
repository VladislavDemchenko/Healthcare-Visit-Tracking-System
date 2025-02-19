package org.demchenko.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PatientResponse implements Serializable {
        private String firstName;

        private String lastName;

        private List<VisitDto> listVisits;
}

