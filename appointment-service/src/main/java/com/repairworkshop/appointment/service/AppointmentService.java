package com.repairworkshop.appointment.service;

import com.repairworkshop.appointment.client.TechnicianServiceClient;
import com.repairworkshop.appointment.dto.AppointmentRequestDTO;
import com.repairworkshop.appointment.dto.AppointmentResponseDTO;
import com.repairworkshop.appointment.dto.TechnicianDTO;
import com.repairworkshop.appointment.entity.Appointment;
import com.repairworkshop.appointment.enums.AppointmentStatus;
import com.repairworkshop.appointment.exception.ResourceNotFoundException;
import com.repairworkshop.appointment.messaging.RepairEventPublisher;
import com.repairworkshop.appointment.repository.AppointmentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final TechnicianServiceClient technicianServiceClient;
    private final RepairEventPublisher eventPublisher;

    // ─── CREATE ────────────────────────────────────────────────────────────────

    @CircuitBreaker(name = "technicianService", fallbackMethod = "createAppointmentFallback")
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
        logger.info("Creating appointment for customer: {}", request.getCustomerName());

        // Synchronous call to Technician Service via OpenFeign
        logger.info("Calling Technician Service for specialisation: {}", request.getRepairCategory());
        TechnicianDTO technician = technicianServiceClient
                .findAvailableTechnician(request.getRepairCategory().name());

        Appointment appointment = Appointment.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .itemDescription(request.getItemDescription())
                .repairCategory(request.getRepairCategory())
                .scheduledDate(request.getScheduledDate())
                .status(AppointmentStatus.CONFIRMED)
                .assignedTechnicianId(technician.getId())
                .assignedTechnicianName(technician.getName())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        logger.info("Appointment {} created and CONFIRMED with technician: {}",
                saved.getId(), technician.getName());

        return mapToResponse(saved, "Appointment confirmed. Technician assigned successfully.");
    }

    // Fallback when Technician Service is unavailable
    public AppointmentResponseDTO createAppointmentFallback(
            AppointmentRequestDTO request, Exception ex) {
        logger.warn("Technician Service unavailable. Saving appointment as PENDING. Error: {}",
                ex.getMessage());

        Appointment appointment = Appointment.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .itemDescription(request.getItemDescription())
                .repairCategory(request.getRepairCategory())
                .scheduledDate(request.getScheduledDate())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved,
                "Technician service temporarily unavailable. " +
                "Appointment saved as PENDING. A technician will be assigned shortly.");
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(a -> mapToResponse(a, null))
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return mapToResponse(appointment, null);
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        appointment.setCustomerName(request.getCustomerName());
        appointment.setCustomerEmail(request.getCustomerEmail());
        appointment.setItemDescription(request.getItemDescription());
        appointment.setRepairCategory(request.getRepairCategory());
        appointment.setScheduledDate(request.getScheduledDate());

        return mapToResponse(appointmentRepository.save(appointment), "Appointment updated.");
    }

    // ─── STATUS UPDATE (triggers async event on COMPLETED) ────────────────────

    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        AppointmentStatus previousStatus = appointment.getStatus();
        appointment.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);

        // Publish async event when appointment is completed
        if (newStatus == AppointmentStatus.COMPLETED
                && saved.getAssignedTechnicianId() != null) {
            logger.info("Appointment {} COMPLETED — publishing RepairCompletedEvent", id);
            eventPublisher.publishRepairCompleted(
                    saved.getId(),
                    saved.getAssignedTechnicianId(),
                    saved.getRepairCategory().name()
            );
        }

        return mapToResponse(saved, "Status updated from " + previousStatus + " to " + newStatus);
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment", id);
        }
        appointmentRepository.deleteById(id);
        logger.info("Appointment {} deleted", id);
    }

    // ─── MAPPER ────────────────────────────────────────────────────────────────

    private AppointmentResponseDTO mapToResponse(Appointment a, String message) {
        return AppointmentResponseDTO.builder()
                .id(a.getId())
                .customerName(a.getCustomerName())
                .customerEmail(a.getCustomerEmail())
                .itemDescription(a.getItemDescription())
                .repairCategory(a.getRepairCategory())
                .status(a.getStatus())
                .assignedTechnicianId(a.getAssignedTechnicianId())
                .assignedTechnicianName(a.getAssignedTechnicianName())
                .scheduledDate(a.getScheduledDate())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .message(message)
                .build();
    }
}
