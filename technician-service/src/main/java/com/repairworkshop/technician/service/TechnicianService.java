package com.repairworkshop.technician.service;

import com.repairworkshop.technician.dto.TechnicianRequestDTO;
import com.repairworkshop.technician.dto.TechnicianResponseDTO;
import com.repairworkshop.technician.entity.Technician;
import com.repairworkshop.technician.enums.Specialisation;
import com.repairworkshop.technician.enums.TechnicianStatus;
import com.repairworkshop.technician.exception.ResourceNotFoundException;
import com.repairworkshop.technician.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianService {

    private static final Logger logger = LoggerFactory.getLogger(TechnicianService.class);

    private final TechnicianRepository technicianRepository;

    // ─── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public TechnicianResponseDTO createTechnician(TechnicianRequestDTO request) {
        if (technicianRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Technician with email " + request.getEmail() + " already exists");
        }

        Technician technician = Technician.builder()
                .name(request.getName())
                .email(request.getEmail())
                .specialisation(request.getSpecialisation())
                .status(request.getStatus() != null
                        ? request.getStatus() : TechnicianStatus.AVAILABLE)
                .completedJobCount(0)
                .build();

        Technician saved = technicianRepository.save(technician);
        logger.info("Technician created: {} ({})", saved.getName(), saved.getSpecialisation());
        return mapToResponse(saved);
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public List<TechnicianResponseDTO> getAllTechnicians() {
        return technicianRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TechnicianResponseDTO getTechnicianById(Long id) {
        return mapToResponse(findById(id));
    }

    // Called by Appointment Service via OpenFeign
    public TechnicianResponseDTO findAvailableTechnician(String specialisation) {
        logger.info("Feign call received — finding available technician for: {}",
                specialisation);

        Specialisation spec;
        try {
            spec = Specialisation.valueOf(specialisation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown specialisation: " + specialisation);
        }

        Technician technician = technicianRepository
                .findFirstBySpecialisationAndStatus(spec, TechnicianStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No available technician found for specialisation: " + specialisation));

        logger.info("Available technician found: {} for {}", technician.getName(), specialisation);
        return mapToResponse(technician);
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public TechnicianResponseDTO updateTechnician(Long id, TechnicianRequestDTO request) {
        Technician technician = findById(id);

        technician.setName(request.getName());
        technician.setEmail(request.getEmail());
        technician.setSpecialisation(request.getSpecialisation());
        if (request.getStatus() != null) {
            technician.setStatus(request.getStatus());
        }

        return mapToResponse(technicianRepository.save(technician));
    }

    @Transactional
    public TechnicianResponseDTO updateStatus(Long id, TechnicianStatus newStatus) {
        Technician technician = findById(id);
        TechnicianStatus previous = technician.getStatus();
        technician.setStatus(newStatus);
        technicianRepository.save(technician);
        logger.info("Technician {} status: {} → {}", technician.getName(), previous, newStatus);
        return mapToResponse(technician);
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteTechnician(Long id) {
        if (!technicianRepository.existsById(id)) {
            throw new ResourceNotFoundException("Technician", id);
        }
        technicianRepository.deleteById(id);
        logger.info("Technician {} deleted", id);
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    private Technician findById(Long id) {
        return technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", id));
    }

    private TechnicianResponseDTO mapToResponse(Technician t) {
        return TechnicianResponseDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .email(t.getEmail())
                .specialisation(t.getSpecialisation())
                .status(t.getStatus())
                .completedJobCount(t.getCompletedJobCount())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
