package com.repairworkshop.appointment.client;

import com.repairworkshop.appointment.dto.TechnicianDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "technician-service")
public interface TechnicianServiceClient {

    @GetMapping("/api/technicians/available")
    TechnicianDTO findAvailableTechnician(
            @RequestParam("specialisation") String specialisation
    );
}
