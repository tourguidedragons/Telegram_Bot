package com.example.tourbot.service;

import com.example.tourbot.models.Option;
import com.example.tourbot.models.Question;

public interface QuestionService {
    Question getQuestionByKey(String key);
    Boolean  isButton(Question question);
    Question getNextQuestion(Question question,String answer);
    String getQuestionTranslation(Question question, String code);
}
