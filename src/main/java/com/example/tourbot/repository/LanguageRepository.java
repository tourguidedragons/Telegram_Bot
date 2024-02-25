package com.example.tourbot.repository;

import com.example.tourbot.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Language getByCode(String code);
}

