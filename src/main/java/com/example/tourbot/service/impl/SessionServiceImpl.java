package com.example.tourbot.service.impl;

import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.models.Session;
import com.example.tourbot.repository.SessionRepository;
import com.example.tourbot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository repository;

    @Override
    public void create(CurrentSession currentSession) {
        Session session = Session.builder()
                .chatId(currentSession.getChatId())
                .expireDate(LocalDateTime.now().plusHours(1))
                .build();
         save(session);
    }

    @Override
    public Session save(Session request) {
        return repository.save(request);
    }
    @Override
    public List<Session> findByClientId(Long clientId) {
        return repository.findByClientId(clientId);
    }

}
