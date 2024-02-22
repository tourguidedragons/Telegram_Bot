package com.example.tourbot.service.impl;

import com.example.tourbot.enums.OptionType;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Translation;
import com.example.tourbot.models.Option;
import com.example.tourbot.repository.QuestionRepository;
import com.example.tourbot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question getQuestionByKey(String key) {
        return questionRepository.getQuestionsByKey(key);
    }

    public Boolean isText(Question question) {
        return question.getOptions().stream()
                .anyMatch(a -> a.getOptionType() == OptionType.BUTTON);
    }

    @Override
    public Question getNextQuestion(Question question, String answer) {
        return isText(question) ? getTextOption(question) : getButtonOption(question, answer);
    }

    private Question getButtonOption(Question question, String answer) {
        return question.getOptions().stream()
                .filter(option ->
                        option.getAnswer().equals(answer) ||
                                option.getTranslations().stream()
                                        .anyMatch(translation -> translation.getTranslatedText().equals(answer))
                )
                .map(Option::getNextQuestion)
                .findFirst()
                .orElse(null);
    }

    private Question getTextOption(Question question) {
        Option option = question.getOptions().stream()
                .findFirst().orElse(null);
        return option == null ? null : option.getNextQuestion();
    }


    @Override
    public String getQuestionTranslation(Question question, String code) {
        Translation translation = question.getTranslations().stream()
                .filter(t -> t.getLanguage().getCode().equals(code))
                .findFirst().orElse(null);

        return translation==null? question.getContent(): translation.getTranslatedText();
    }

    @Override
    public String getOptionTranslation(Option option, String code) {
        Translation translation = option.getTranslations().stream()
                .filter(t -> t.getLanguage().getCode().equals(code))
                .findFirst().orElse(null);

        return translation==null? option.getAnswer(): translation.getTranslatedText();
    }

}
