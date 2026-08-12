package com.repairworkshop.appointment.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairCompletedEvent implements Serializable {
    private String eventId;
    private Long appointmentId;
    private Long technicianId;
    private String repairCategory;
    private LocalDateTime completedAt;
}
