package com.repairworkshop.appointment.messaging;

import com.repairworkshop.appointment.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepairEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RepairEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public void publishRepairCompleted(Long appointmentId, Long technicianId, String repairCategory) {
        RepairCompletedEvent event = new RepairCompletedEvent(
                UUID.randomUUID().toString(),
                appointmentId,
                technicianId,
                repairCategory,
                LocalDateTime.now()
        );

        logger.info("Publishing RepairCompletedEvent: appointmentId={}, technicianId={}",
                appointmentId, technicianId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        logger.info("RepairCompletedEvent published successfully to exchange: {}",
                RabbitMQConfig.EXCHANGE_NAME);
    }
}
