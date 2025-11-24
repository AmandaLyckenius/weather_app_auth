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

        CustomUser user = customUserRepository.findById(userLookupRequestDTO.id())
                .orElseThrow(() -> new UserNotFoundException(userLookupRequestDTO.id()));

        UserLookupResponseDTO responseDTO = new UserLookupResponseDTO(user.getEmail());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.REQUEST_ROUTING_KEY,
                responseDTO
        );

    }
}
