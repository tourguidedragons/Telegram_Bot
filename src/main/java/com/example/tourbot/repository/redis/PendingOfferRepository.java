package com.example.tourbot.repository.redis;

import com.example.tourbot.dto.OfferDto;
import com.example.tourbot.models.Offer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PendingOfferRepository extends CrudRepository<OfferDto, Long> {
    List<OfferDto> findAllByid(Long id);
    List<OfferDto> findAllByUuid(UUID uuid);

}
