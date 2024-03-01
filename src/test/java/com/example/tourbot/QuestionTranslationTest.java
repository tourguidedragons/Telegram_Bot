package com.example.tourbot;

import com.example.tourbot.enums.OptionType;
import com.example.tourbot.models.Language;
import com.example.tourbot.models.Option;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Translation;
import com.example.tourbot.repository.QuestionRepository;
import com.example.tourbot.service.QuestionService;
import com.example.tourbot.service.impl.QuestionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestionTranslationTest {
    @Mock
    private QuestionRepository repository;
    QuestionService service;
    static Question question;

    @BeforeAll
     void executeBefore() {
        MockitoAnnotations.openMocks(QuestionRepository.class);
        service = new QuestionServiceImpl(repository);
        question = Question.builder()
                .key("test")
                .content("Unit test")
                .pattern("")
                .translations(List.of(
                        Translation.builder().translatedText("Hello, this is simple test").language(Language.builder().code("EN").build()).build(),
                        Translation.builder().translatedText("Bu bir testdir").language(Language.builder().code("AZ").build()).build(),
                       Translation.builder().translatedText("Тестирование").language(Language.builder().code("RU").build()).build()
                ))
                .build();
    }

    @Test
    @DisplayName("Test English translation")
    void testGetQuestionTranslationInEnglish() {
        String translation = service.getQuestionTranslation(question, "EN");
        Assertions.assertEquals("Hello, this is simple test",  translation);
    }

    @Test
    @DisplayName("Test Spanish translation")
    void testGetQuestionTranslationInSpanish() {
        String translation = service.getQuestionTranslation(question, "ES");
        Assertions.assertEquals("Unit test", translation);
    }

    @Test
    @DisplayName("Test Russian translation")
    void testGetQuestionTranslationInRussian() {
        String translation = service.getQuestionTranslation(question, "RU");
        Assertions.assertEquals("Тестирование", translation);
    }

    @Test
    @DisplayName("Test Azerbaijani translation")
    void testGetQuestionTranslationInAzerbaijani() {
        String translation = service.getQuestionTranslation(question, "AZ");
        Assertions.assertEquals("Bu bir testdir", translation);
    }


}
