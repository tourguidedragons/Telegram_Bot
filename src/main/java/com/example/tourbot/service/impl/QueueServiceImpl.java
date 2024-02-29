package com.example.tourbot.service.impl;

import com.example.tourbot.dto.SelectedOfferDto;
import com.example.tourbot.dto.SessionDto;
import com.example.tourbot.service.QueueService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.tourbot.config.*;

@Service
public class QueueServiceImpl implements QueueService {
    @Autowired
    private RabbitTemplate template;
    @Override
    public void sendRequestToQueue(SessionDto session) {
        template.convertAndSend(RabbitConfig.QUEUE, session);
    }

    @Override
    public void sendSelectedToQueue(SelectedOfferDto offer) {

    }
}
