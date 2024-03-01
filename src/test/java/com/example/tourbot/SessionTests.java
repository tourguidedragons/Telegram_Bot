package com.example.tourbot;

import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.models.Session;
import com.example.tourbot.repository.SessionRepository;
import com.example.tourbot.service.SessionService;
import com.example.tourbot.service.impl.SessionServiceImpl;
import org.glassfish.grizzly.http.server.SessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionTests {

    @Mock
    private CurrentSession mockCurrentSession;

    @Mock
    private SessionRepository mockRepository;
    private SessionService sessionManager;

    private Clock fixedTime;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionManager = new SessionServiceImpl(mockRepository);
        fixedTime = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    @Test
    public void testCreate() {
        long chatId = 12345L;
        when(mockCurrentSession.getChatId()).thenReturn(chatId);
        sessionManager.create(mockCurrentSession);
        LocalDateTime expectedExpireDate = LocalDateTime.now(fixedTime).plusHours(1);
        when(mockRepository.save(any())).thenReturn(Session.builder()
                .chatId(chatId)
                .expireDate(expectedExpireDate)
                .build());

    }
}

