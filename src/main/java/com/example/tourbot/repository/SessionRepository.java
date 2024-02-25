package com.example.tourbot.repository;

import com.example.tourbot.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByClientId(Long clientId);
}
