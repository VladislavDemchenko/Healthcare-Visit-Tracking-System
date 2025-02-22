package org.demchenko.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class PatientDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
}
