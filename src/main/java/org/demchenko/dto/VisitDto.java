package org.demchenko.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class VisitDto implements Serializable {
        private String start;
        private String end;
        private DoctorDto doctor;
}
