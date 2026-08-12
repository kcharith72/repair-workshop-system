package com.repairworkshop.technician.dto;

import com.repairworkshop.technician.enums.Specialisation;
import com.repairworkshop.technician.enums.TechnicianStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Specialisation specialisation;
    private TechnicianStatus status;
    private Integer completedJobCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
