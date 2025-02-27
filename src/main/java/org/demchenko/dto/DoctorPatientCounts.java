package org.demchenko.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class DoctorPatientCounts implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long doctorId;
    private Long patientCount;
}
