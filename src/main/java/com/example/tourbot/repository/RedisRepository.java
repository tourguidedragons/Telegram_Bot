package com.example.tourbot.repository;

import com.example.tourbot.models.CurrentSession;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRepository extends CrudRepository<CurrentSession, Long> {
    Optional<CurrentSession> findById(Long id);
}
