package com.repairworkshop.technician.messaging;

import com.repairworkshop.technician.entity.Technician;
import com.repairworkshop.technician.enums.TechnicianStatus;
import com.repairworkshop.technician.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RepairEventConsumer.class);

    private final TechnicianRepository technicianRepository;

    @RabbitListener(queues = "repair.completed.queue")
    @Transactional
    public void handleRepairCompleted(RepairCompletedEvent event) {
        logger.info("============================================");
        logger.info("Received RepairCompletedEvent:");
        logger.info("  Event ID       : {}", event.getEventId());
        logger.info("  Appointment ID : {}", event.getAppointmentId());
        logger.info("  Technician ID  : {}", event.getTechnicianId());
        logger.info("  Category       : {}", event.getRepairCategory());
        logger.info("  Completed At   : {}", event.getCompletedAt());
        logger.info("============================================");

        technicianRepository.findById(event.getTechnicianId()).ifPresentOrElse(
            technician -> {
                // Update technician stats based on event
                int previousCount = technician.getCompletedJobCount();
                technician.setCompletedJobCount(previousCount + 1);
                technician.setStatus(TechnicianStatus.AVAILABLE);
                technicianRepository.save(technician);

                logger.info("Technician {} updated: completedJobs {} → {}, status → AVAILABLE",
                        technician.getName(), previousCount, technician.getCompletedJobCount());
            },
            () -> logger.warn("Technician with id {} not found — event skipped",
                    event.getTechnicianId())
        );
    }
}
