package org.demchenko.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class DoctorDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private String firstName;
        private String lastName;
        private Long totalPatients;
}
