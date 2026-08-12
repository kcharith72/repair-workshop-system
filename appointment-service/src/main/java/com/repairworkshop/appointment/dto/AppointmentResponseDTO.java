package com.repairworkshop.appointment.dto;

import com.repairworkshop.appointment.enums.AppointmentStatus;
import com.repairworkshop.appointment.enums.RepairCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String itemDescription;
    private RepairCategory repairCategory;
    private AppointmentStatus status;
    private Long assignedTechnicianId;
    private String assignedTechnicianName;
    private LocalDate scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message; // Used for fallback messages
}
