package com.amanda.weather_app_auth.service;

import com.amanda.weather_app_auth.config.RabbitConfig;
import com.amanda.weather_app_auth.dto.UserLookupRequestDTO;
import com.amanda.weather_app_auth.dto.UserLookupResponseDTO;
import com.amanda.weather_app_auth.exception.UserNotFoundException;
import com.amanda.weather_app_auth.user.CustomUser;
import com.amanda.weather_app_auth.user.CustomUserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserLookupListener {
    private final CustomUserRepository customUserRepository;
    private final RabbitTemplate rabbitTemplate;


    public UserLookupListener(CustomUserRepository customUserRepository, RabbitTemplate rabbitTemplate) {
        this.customUserRepository = customUserRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.REQUEST_QUEUE)
    public void handleUserLookup(UserLookupRequestDTO userLookupRequestDTO){

        System.out.println("🔥 Received UserLookupRequestDTO: " + userLookupRequestDTO);
        System.out.println("🔥 userId = " + userLookupRequestDTO.userId());

        if (userLookupRequestDTO.userId() == null) {
            System.err.println("❌ userId i UserLookupRequestDTO är null – kan inte kalla findById");
            // Här kan du antingen return:a, eller kasta en mer beskrivande exception
            return;
            // eller:
            // throw new IllegalArgumentException("userId must not be null");
        }

        CustomUser user = customUserRepository.findByUserId(userLookupRequestDTO.userId())
                .orElseThrow(() -> new UserNotFoundException(userLookupRequestDTO.userId()));

        UserLookupResponseDTO responseDTO = new UserLookupResponseDTO(user.getEmail());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.RESPONSE_ROUTING_KEY,
                responseDTO
        );

    }
}
