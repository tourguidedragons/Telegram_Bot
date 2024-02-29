package com.example.tourbot.service;

import com.example.tourbot.dto.SelectedOfferDto;
import com.example.tourbot.dto.SessionDto;

public interface QueueService {

    void sendRequestToQueue(SessionDto session);
    void sendSelectedToQueue(SelectedOfferDto offer);
}
