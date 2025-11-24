package com.amanda.weather_app_auth.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Queues
    public static final String REQUEST_QUEUE = "auth-request-queue";   // tar emot från Notification Service
    public static final String RESPONSE_QUEUE = "auth-response-queue"; // skickar till Notification Service

    // Exchange
    public static final String EXCHANGE = "weather-exchange";

    // Routing keys
    public static final String REQUEST_ROUTING_KEY = "auth.request";
    public static final String RESPONSE_ROUTING_KEY = "auth.response";

    @Bean
    public Queue requestQueue() {
        return new Queue(REQUEST_QUEUE, true);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(RESPONSE_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(exchange())
                .with(REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding responseBinding() {
        return BindingBuilder
                .bind(responseQueue())
                .to(exchange())
                .with(RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate t = new RabbitTemplate(connectionFactory);
        t.setMessageConverter(converter());
        return t;
    }
}
