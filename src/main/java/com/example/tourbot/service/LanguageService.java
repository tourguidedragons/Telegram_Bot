package com.example.tourbot.service;

import com.example.tourbot.models.Language;

public interface LanguageService {
    Language getByName(String language);
}