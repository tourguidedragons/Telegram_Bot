package com.example.tourbot.service;

import com.example.tourbot.dto.OfferDto;
import com.example.tourbot.dto.SessionDto;
import com.example.tourbot.models.Session;

public interface QueueService {

    void sendRequestToQueue(SessionDto session);
    void sendSelectedToQueue(OfferDto offer);
}
