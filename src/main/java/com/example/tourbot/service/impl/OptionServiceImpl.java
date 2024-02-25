package com.example.tourbot.service.impl;

import com.example.tourbot.models.Option;
import com.example.tourbot.models.Translation;
import com.example.tourbot.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionServiceImpl implements OptionService {
    @Override
    public String getOptionTranslation(Option option, String code) {
        Translation translation = option.getTranslations().stream()
                .filter(t -> t.getLanguage().getCode().equals(code))
                .findFirst().orElse(null);

        return translation == null ? option.getAnswer() : translation.getTranslatedText();
    }

    @Override
    public List<Translation> getOptionTranslations(Option option) {
    return option.getTranslations();
    }
}
