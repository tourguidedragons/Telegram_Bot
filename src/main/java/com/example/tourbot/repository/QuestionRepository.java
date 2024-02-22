package com.example.tourbot.repository;

import com.example.tourbot.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question getQuestionsByKey(String key);
}