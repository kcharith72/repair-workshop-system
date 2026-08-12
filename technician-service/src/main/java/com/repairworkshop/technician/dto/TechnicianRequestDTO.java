package com.repairworkshop.technician.dto;

import com.repairworkshop.technician.enums.Specialisation;
import com.repairworkshop.technician.enums.TechnicianStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Specialisation is required")
    private Specialisation specialisation;

    private TechnicianStatus status;
}
