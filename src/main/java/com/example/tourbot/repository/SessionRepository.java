package com.example.tourbot.repository;

import com.example.tourbot.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByClientId(Long clientId);

    @Query(value = "UPDATE requests SET is_active = FALSE WHERE expire_date < now() AND  expire_date IS NOT NULL AND is_active = TRUE", nativeQuery = true)
    void expireSessions();

    Session findByuuid(UUID uuid);
}
