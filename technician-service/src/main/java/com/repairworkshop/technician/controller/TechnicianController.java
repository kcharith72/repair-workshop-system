package com.repairworkshop.technician.controller;

import com.repairworkshop.technician.dto.TechnicianRequestDTO;
import com.repairworkshop.technician.dto.TechnicianResponseDTO;
import com.repairworkshop.technician.enums.TechnicianStatus;
import com.repairworkshop.technician.service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;

    // POST /api/technicians — Create (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicianResponseDTO> createTechnician(
            @Valid @RequestBody TechnicianRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(technicianService.createTechnician(request));
    }

    // GET /api/technicians — Read All (USER or ADMIN)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<TechnicianResponseDTO>> getAllTechnicians() {
        return ResponseEntity.ok(technicianService.getAllTechnicians());
    }

    // GET /api/technicians/{id} — Read One (USER or ADMIN)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TechnicianResponseDTO> getTechnicianById(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.getTechnicianById(id));
    }

    // GET /api/technicians/available?specialisation=X
    // Called internally by Appointment Service via OpenFeign
    @GetMapping("/available")
    public ResponseEntity<TechnicianResponseDTO> findAvailableTechnician(
            @RequestParam String specialisation) {
        return ResponseEntity.ok(
                technicianService.findAvailableTechnician(specialisation));
    }

    // PUT /api/technicians/{id} — Update (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicianResponseDTO> updateTechnician(
            @PathVariable Long id,
            @Valid @RequestBody TechnicianRequestDTO request) {
        return ResponseEntity.ok(technicianService.updateTechnician(id, request));
    }

    // PATCH /api/technicians/{id}/status — Update Status (ADMIN only)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicianResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TechnicianStatus status) {
        return ResponseEntity.ok(technicianService.updateStatus(id, status));
    }

    // DELETE /api/technicians/{id} — Delete (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTechnician(@PathVariable Long id) {
        technicianService.deleteTechnician(id);
        return ResponseEntity.noContent().build();
    }
}
