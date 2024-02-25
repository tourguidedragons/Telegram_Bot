package com.example.tourbot.service;

import com.example.tourbot.models.Option;
import com.example.tourbot.models.Translation;

import java.util.List;

public interface OptionService  {

    String getOptionTranslation(Option option, String code);

    List<Translation> getOptionTranslations(Option option);
}
