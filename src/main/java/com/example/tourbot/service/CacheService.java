package com.example.tourbot.service;

import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.models.Question;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CacheService {
    void createSession(Message message);
    void stopSession(Message message);
    void  setSelectedLanguage(Long clientId, String language);
    String getCurrentLanguage(long clientId);
    CurrentSession find(Long clientId);
    void setCurrentStateQuestion(Long clientId, Question question);
    String getCurrentStateQuestion(Long clientId);
    void saveUserAnswer(Long clientId, String key, String answer);

}

