package com.example.tourbot.service.impl;

import com.example.tourbot.bot.Bot;
import com.example.tourbot.exception.CurrentSessionNotFoundException;
import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.models.Language;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Session;
import com.example.tourbot.repository.RedisRepository;
import com.example.tourbot.service.CacheService;
import com.example.tourbot.service.LanguageService;
import com.example.tourbot.service.SessionService;
import com.example.tourbot.utils.SampleAnswers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class CacheServiceImpl implements CacheService {
    private final RedisRepository redisRepository;
    private final LanguageService languageService;
    private final SessionService sessionService;
private final Bot bot;
    public CacheServiceImpl(RedisRepository redisRepository,LanguageService languageService, SessionService sessionService, @Lazy Bot bot) {
        this.redisRepository = redisRepository;
        this.languageService = languageService;
        this.sessionService = sessionService;
        this.bot = bot;
    }
    @Override
    public void createSession(Message message) {
        CurrentSession session = CurrentSession.builder()
                .UUID(UUID.randomUUID().toString())
                .id(message.getFrom().getId())
                .question(null)
                .history(null)
                .lang(null)
                .chatId(message.getChatId())
                .build();
        redisRepository.save(session);
    }

    @Override
    public CurrentSession find(Long clientId) {
        return redisRepository.findById(clientId)
                .orElseThrow(() -> new CurrentSessionNotFoundException("CurrentSession not found with clientId: " + clientId));
    }


    @Override
    public void setCurrentStateQuestion(Long clientId, Question question) {
        CurrentSession session = find(clientId);
        session.setQuestion(question.getKey());
        redisRepository.save(session);
    }

    @Override
    public String getCurrentStateQuestion(Long clientId) {
        CurrentSession session = find(clientId);
        return session.getQuestion();
    }

    @Override
    public void setSelectedLanguage(Long clientId, String language) {
        Language lang = languageService.getByName(language);
        String code = "AZ";
        if (lang != null) {
            code = lang.getCode();
        }
        CurrentSession session = find(clientId);
        session.setLang(code);
        redisRepository.save(session);
    }

    @Override
    public String getCurrentLanguage(long clientId) {
        return find(clientId).getLang();
    }

    @Override
    public void saveUserAnswer(Long clientId, String key, String answer) {
        CurrentSession session = find(clientId);
        Map<String, String> userData = session.getHistory();
        userData.put(key, answer);
        session.setHistory(userData);
        redisRepository.save(session);
    }

    @Override
    public LocalDate getCurrentDate(Long clientId) {
        CurrentSession session = find(clientId);
        if (session.getDate() == null) return LocalDate.now();
        return session.getDate();
    }

    @Override
    public void setCurrentDate(Long clientId, LocalDate date) {
        CurrentSession session = find(clientId);
        session.setDate(date);
        redisRepository.save(session);
    }

    @Override
    public void endCurrentSession(Long clientId) {
        CurrentSession session = find(clientId);
        sessionService.create(session);
        session.setHistory(null);
        redisRepository.save(session);
    }
    @Override
    public void disableActiveSession(Long clientId){
        CurrentSession session = find(clientId);
        redisRepository.delete(session);
        List<Session> requests = sessionService.findByClientId(clientId);
        requests.stream().filter(Session::getIsActive)
                .forEach(r -> {
                    r.setIsActive(false);
                    sessionService.save(r);
                });
    }
    @Override
    public void expiredSession(Session session) {
        String lang = getCurrentLanguage(session.getClientId());
        disableActiveSession(session.getClientId());
        try {
            bot.execute(new SendMessage(session.getChatId().toString(),
                    SampleAnswers.getMessage("",lang)));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
