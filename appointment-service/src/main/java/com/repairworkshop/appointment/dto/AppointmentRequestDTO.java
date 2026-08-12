package com.repairworkshop.appointment.dto;

import com.repairworkshop.appointment.enums.RepairCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @NotBlank(message = "Item description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String itemDescription;

    @NotNull(message = "Repair category is required")
    private RepairCategory repairCategory;

    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    private LocalDate scheduledDate;
}
