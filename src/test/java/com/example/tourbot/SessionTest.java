package com.example.tourbot;

import com.example.tourbot.exception.CurrentSessionNotFoundException;
import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.repository.RedisRepository;
import com.example.tourbot.repository.SessionRepository;
import com.example.tourbot.service.SessionService;
import com.example.tourbot.service.impl.SessionServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionTest {
    @InjectMocks
    SessionServiceImpl service;
    @Mock
    RedisRepository repository;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void whenExceptionThrown_thenAssertionSucceeds() {
        CurrentSession nonexistent = CurrentSession.builder()
                .chatId(3L).lang("EN").UUID(UUID.randomUUID().toString())
                .question("lang").build();
        when (repository.findById(anyLong()))
                .thenReturn(Optional.of(nonexistent))
                .thenThrow(CurrentSessionNotFoundException.class);

    }
}
