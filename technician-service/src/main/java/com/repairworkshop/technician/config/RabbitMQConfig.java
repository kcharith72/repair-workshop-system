package com.repairworkshop.technician.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Must match exactly what Appointment Service publishes to
    public static final String EXCHANGE_NAME = "repair.events.exchange";
    public static final String QUEUE_NAME    = "repair.completed.queue";
    public static final String ROUTING_KEY   = "repair.completed";

    @Bean
    public DirectExchange repairExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue repairCompletedQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding repairBinding(Queue repairCompletedQueue, DirectExchange repairExchange) {
        return BindingBuilder
                .bind(repairCompletedQueue)
                .to(repairExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
