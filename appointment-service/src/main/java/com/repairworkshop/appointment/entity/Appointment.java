package com.repairworkshop.appointment.entity;

import com.repairworkshop.appointment.enums.AppointmentStatus;
import com.repairworkshop.appointment.enums.RepairCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String customerName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Customer email is required")
    @Column(nullable = false)
    private String customerEmail;

    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Column(nullable = false, length = 500)
    private String itemDescription;

    @NotNull(message = "Repair category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairCategory repairCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    // Soft reference to Technician Service — no JPA join across services
    private Long assignedTechnicianId;
    private String assignedTechnicianName;

    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
