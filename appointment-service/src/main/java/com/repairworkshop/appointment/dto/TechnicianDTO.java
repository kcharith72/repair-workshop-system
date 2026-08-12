package com.repairworkshop.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDTO {
    private Long id;
    private String name;
    private String email;
    private String specialisation;
    private String status;
}
