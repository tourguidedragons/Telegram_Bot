package com.example.tourbot.listener;

import com.example.tourbot.config.RabbitConfig;
import com.example.tourbot.dto.OfferDto;
import com.example.tourbot.models.Offer;
import com.example.tourbot.models.Session;
import com.example.tourbot.repository.redis.PendingOfferRepository;
import com.example.tourbot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class OfferListener {

    private final SessionService service;
    private final PendingOfferRepository pendingOfferRepository;

    @Transactional
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consumeMessageFromQueue(OfferDto offerDto) throws TelegramApiException {

        Session session = service.findByUUId(offerDto.getUuid());
        if (session != null) {
            pendingOfferRepository.save(offerDto);
            service.save(session);
        }
    }

}
