package com.example.tourbot;

import com.example.tourbot.enums.OptionType;
import com.example.tourbot.models.Language;
import com.example.tourbot.models.Option;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Translation;
import com.example.tourbot.repository.OptionRepository;
import com.example.tourbot.repository.QuestionRepository;
import com.example.tourbot.repository.RedisRepository;
import com.example.tourbot.service.OptionService;
import com.example.tourbot.service.QuestionService;
import com.example.tourbot.service.impl.OptionServiceImpl;
import com.example.tourbot.service.impl.QuestionServiceImpl;
import com.example.tourbot.service.impl.SessionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OptionTranslationTest {
    @InjectMocks
    OptionService service;
    @Mock
    private OptionRepository repository;
    static Question question;

    @BeforeAll
    void executeBefore() {
        MockitoAnnotations.openMocks(QuestionRepository.class);
        service = new OptionServiceImpl();
        List<Option> options = List.of(Option.builder().optionType(OptionType.BUTTON)
                        .answer("text1").translations(List.of(
                                Translation.builder().translatedText("Click").language(Language.builder().code("EN").build()).build(),
                                Translation.builder().translatedText("Bas").language(Language.builder().code("AZ").build()).build(),
                                Translation.builder().translatedText("Жми").language(Language.builder().code("RU").build()).build()
                        )).build(),
                Option.builder().optionType(OptionType.BUTTON).answer("text2")
                        .translations(List.of(
                                Translation.builder().translatedText("change").language(Language.builder().code("EN").build()).build(),
                                Translation.builder().translatedText("dəyiş").language(Language.builder().code("AZ").build()).build(),
                                Translation.builder().translatedText("изменить").language(Language.builder().code("RU").build()).build()
                        )).build(),
                Option.builder().optionType(OptionType.BUTTON).answer("text3")
                        .translations(List.of(
                                Translation.builder().translatedText("load").language(Language.builder().code("EN").build()).build(),
                                Translation.builder().translatedText("yüklə").language(Language.builder().code("AZ").build()).build(),
                                Translation.builder().translatedText("загрузить").language(Language.builder().code("RU").build()).build()
                        )).build());


        var option =   Option.builder().optionType(OptionType.BUTTON)
                .answer("text1").translations(List.of(
                        Translation.builder().translatedText("Click").language(Language.builder().code("EN").build()).build(),
                        Translation.builder().translatedText("Bas").language(Language.builder().code("AZ").build()).build(),
                        Translation.builder().translatedText("Жми").language(Language.builder().code("RU").build()).build()
                )).build();
    }




    @Test
    @DisplayName("Option - En")
    void getOptionEnglish(){

      var option = Option.builder().optionType(OptionType.BUTTON)
                .answer("text1").translations(List.of(
                        Translation.builder().translatedText("click").language(Language.builder().code("EN").build()).build(),
                        Translation.builder().translatedText("bas").language(Language.builder().code("AZ").build()).build(),
                        Translation.builder().translatedText("жми").language(Language.builder().code("RU").build()).build()
                )).build();
       var translation = service.getOptionTranslation(option, "EN");
        Assertions.assertTrue(translation.equals("click") || translation.equals("text1"));
    }

    @Test
    @DisplayName("Option - RU")
    void getOptionRussian(){

        var option = Option.builder().optionType(OptionType.BUTTON)
                .answer("text1").translations(List.of(
                        Translation.builder().translatedText("click").language(Language.builder().code("EN").build()).build(),
                        Translation.builder().translatedText("bas").language(Language.builder().code("AZ").build()).build()
//                        Translation.builder().translatedText("жми").language(Language.builder().code("RU").build()).build()
                )).build();
        var translation = service.getOptionTranslation(option, "RU");
        Assertions.assertTrue(translation.equals("жми") || translation.equals("text1"));
    }

}
