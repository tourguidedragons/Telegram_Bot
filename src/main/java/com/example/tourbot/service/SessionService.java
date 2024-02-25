package com.example.tourbot.service;

import com.example.tourbot.models.CurrentSession;
import com.example.tourbot.models.Session;

import java.util.List;

public interface SessionService {
    void create(CurrentSession session);
    Session save(Session request);
    List<Session> findByClientId(Long clientId);
}
