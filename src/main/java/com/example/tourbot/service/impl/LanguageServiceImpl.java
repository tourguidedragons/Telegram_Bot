package com.example.tourbot.service.impl;

import com.example.tourbot.models.Language;
import com.example.tourbot.repository.LanguageRepository;
import com.example.tourbot.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository repository;
    @Override
    public Language getByName(String language) {
        return  repository.getByName(language);
    }
}