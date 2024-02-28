package com.example.tourbot;

import com.example.tourbot.enums.OptionType;
import com.example.tourbot.models.Language;
import com.example.tourbot.models.Option;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Translation;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor
public class QuestionTranslationTest {

    static Question question;
    @BeforeAll
    void executeBefore() {
        List<Option> options = List.of(Option.builder().optionType(OptionType.TEXT).answer("text").build());

    question = Question.builder()
                .key("test")
                .content("Unit test")
                .pattern("")
                .translations(List.of(
                        Translation.builder().translatedText("Hello, is is simple test").language(Language.builder().code("EN").build()).build(),
                        Translation.builder().translatedText("Bu bir testdir").language(Language.builder().code("AZ").build()).build(),
                        Translation.builder().translatedText("Тестирование").language(Language.builder().code("RU").build()).build()
                ))
                .build();
    }
    @Test
    @DisplayName("Test English translation")
    void testGetQuestionTranslationInEnglish() {
        String translation = getQuestionTranslation(question, "EN");
        Assertions.assertEquals("Hello, is is simple test", translation);
    }
    @Test
    @DisplayName("Test Russian translation")
    void testGetQuestionTranslationInRussian() {
        String translation = getQuestionTranslation(question, "RU");
        Assertions.assertEquals("Тестирование", translation);
    }
    @Test
    @DisplayName("Test Azerbaijani translation")
    void testGetQuestionTranslationInAzerbaijani() {
        String translation = getQuestionTranslation(question, "AZ");
        Assertions.assertEquals("Bu bir testdir", translation);
    }

    public String getQuestionTranslation(Question question, String code) {
        Translation translation = question.getTranslations().stream()
                .filter(t -> t.getLanguage().getCode().equals(code))
                .findFirst().orElse(null);

        return translation == null ? question.getContent() : translation.getTranslatedText();
    }
}
