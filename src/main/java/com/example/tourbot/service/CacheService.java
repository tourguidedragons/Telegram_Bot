package com.example.tourbot.service;

import com.example.tourbot.dto.OfferDto;
import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.models.Offer;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Session;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CacheService {
    void createSession(Message message);

    void disableActiveSession(Long clientId);

    void endCurrentSession(Long clientId);

    void expiredSession(Session session);

    CurrentSession find(Long clientId);

    void setCurrentStateQuestion(Long clientId, Question question);

    String getCurrentStateQuestion(Long clientId);

    void saveUserAnswer(Long clientId, String key, String answer);

    LocalDate getCurrentDate(Long clientId);

    void setCurrentDate(Long clientId, LocalDate date);

    void setSelectedLanguage(Long clientId, String language);

    String getCurrentLanguage(long clientId);

    List<OfferDto> getPendingOffers(UUID uuid);

   void clearPendingOffers(OfferDto offer);

    int pendingCount(UUID uuid);
}

