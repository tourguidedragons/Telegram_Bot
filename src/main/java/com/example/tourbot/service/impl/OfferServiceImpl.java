package com.example.tourbot.service.impl;

import com.example.tourbot.models.Offer;
import com.example.tourbot.repository.OfferRepository;
import com.example.tourbot.repository.redis.PendingOfferRepository;
import com.example.tourbot.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
   private final OfferRepository offerRepository;
    @Override
    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }


}
