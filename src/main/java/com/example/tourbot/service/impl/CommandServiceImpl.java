package com.example.tourbot.service.impl;

import com.example.tourbot.bot.Bot;
import com.example.tourbot.exception.CurrentSessionNotFoundException;
import com.example.tourbot.models.Question;
import com.example.tourbot.service.CacheService;
import com.example.tourbot.service.CommandService;
import com.example.tourbot.service.OptionService;
import com.example.tourbot.service.QuestionService;
import com.example.tourbot.utils.SampleAnswers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.tourbot.utils.Calendar.generateKeyboard;

@Slf4j
@Service
public class CommandServiceImpl implements CommandService {
    private final QuestionService questionService;
    private final OptionService optionService;

    private final CacheService cacheService;
    private final Bot bot;

    public CommandServiceImpl(QuestionService questionService, OptionService optionService, CacheService cacheService, @Lazy Bot bot) {
        this.questionService = questionService;
        this.optionService = optionService;
        this.cacheService = cacheService;
        this.bot = bot;
    }

    public BotApiMethod<?> validateCallBackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();

        Message message = (Message) callbackQuery.getMessage();
        Integer messageId = message.getMessageId();

        long clientId = callbackQuery.getFrom().getId();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String answer = callbackQuery.getData();
        String questionKey = cacheService.getCurrentStateQuestion(clientId);
        Question question = questionService.getQuestionByKey(questionKey);

        if (question == null) return null;
        if (question.getKey().equals("language")) {
            cacheService.setSelectedLanguage(clientId, answer);

        }
        if (answer.equals("<") || answer.equals(">")) {
            handleCalendar(messageId, update.getCallbackQuery().getInlineMessageId(), clientId, answer);
            return null;
        }

        if (!validateQuestionAnswer(question, answer, clientId)) {
            handleIncorrectAnswer(chatId, messageId, clientId);
            return postQuestion(chatId, clientId, questionService.getQuestionByKey(questionKey));
        }
        try {

            bot.execute(new DeleteMessage(chatId, messageId));
            bot.execute(new SendMessage(chatId, questionService.getQuestionTranslation(question, cacheService.getCurrentLanguage(clientId))));
            bot.execute(new SendMessage(chatId, answer));

        } catch (TelegramApiRequestException e) {
            throw new RuntimeException();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        cacheService.saveUserAnswer(clientId, question.getKey(), answer);
        return postNewQuestion(chatId, clientId, questionService.getNextQuestion(question, answer));

    }

    public BotApiMethod<?> validateCommands(Message message) {
        String chatId = message.getChatId().toString();
        long clientId = message.getFrom().getId();
        if (message.getText().equals("/start")) {

            if (getActiveSession(clientId)) {
                var currentLang = cacheService.getCurrentLanguage(clientId);
                return new SendMessage(chatId, SampleAnswers.getMessage("activeSession", currentLang));
            } else {
                cacheService.createSession(message);
                var question = questionService.getQuestionByKey("language");
                if (question.getKey().equals("language")) {
                    cacheService.setCurrentStateQuestion(clientId, question);
                    return showQuestionKeyboard(question, chatId, SampleAnswers.getMessage("languageSetup", "AZ"));
                }
            }
        } else if (message.getText().equals("/stop")) {
            if (getActiveSession(clientId)) {
                String lang = cacheService.getCurrentLanguage(clientId);
                cacheService.stopSession(message);
                return new SendMessage(chatId, SampleAnswers.getMessage("stop", lang));
            } else {
                return new SendMessage(chatId, SampleAnswers.getMessage("stopActiveSession", "EN"));
            }
        }
        return new SendMessage(chatId, "Unrecognized");

    }
    public BotApiMethod<?> validateReplyMessage(Message message) {

        long clientId = message.getFrom().getId();
        String currentKey = cacheService.getCurrentStateQuestion(clientId);
        Question current = questionService.getQuestionByKey(currentKey);
        cacheService.saveUserAnswer(clientId, current.getKey(), message.getText());
        return null;
    }

    public BotApiMethod<?> validateMessage(Message message) {
        Long clientId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        String answer = message.getText();
        String currentKey = cacheService.getCurrentStateQuestion(clientId);
        Question question = questionService.getQuestionByKey(currentKey);
        String currentLang = cacheService.getCurrentLanguage(clientId);
        if (!validateQuestionAnswer(question, answer)) {
            try {
                bot.execute(new SendMessage(chatId, SampleAnswers.getMessage("incorrectAnswer", currentLang)));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return postNewQuestion(chatId, clientId, questionService.getQuestionByKey(currentKey));
        }
        return postNewQuestion(chatId, clientId, questionService.getNextQuestion(question, answer));
    }

    public Boolean getActiveSession(Long clientId) {
        try {
            return cacheService.find(clientId) != null;
        } catch (CurrentSessionNotFoundException exception) {
            log.error("No active session found for clientId: {}", clientId);
            return false;
        }
    }

    private SendMessage sendMessage(String chatId, String textMessage, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (textMessage != null && !textMessage.isEmpty()) message.setText(textMessage);
        else return null;
        if (keyboard != null) message.setReplyMarkup(keyboard);
        return message;
    }

    private BotApiMethod<?> showQuestionKeyboard(Question question, String chatId, String text, String code) {
        Map<String, String> buttons = new LinkedHashMap<>();

        question.getOptions()
                .forEach(o -> buttons.put(optionService.getOptionTranslation(o, code), optionService.getOptionTranslation(o, code)));
        return sendMessage(chatId, text, Button.maker(buttons));
    }

    private BotApiMethod<?> showQuestionKeyboard(Question question, String chatId, String text) {
        Map<String, String> buttons = new LinkedHashMap<>();
        question.getOptions()
                .forEach(o -> buttons.put(o.getAnswer(), o.getAnswer()));
        return sendMessage(chatId, text, Button.maker(buttons));
    }

    }

    private void handleCalendar(Integer messageId, String inlineId, Long clientId, String answer) {
        LocalDate currentDate = cacheService.getCurrentDate(clientId);
        if (answer.equals(">")) {
            currentDate = currentDate.plusMonths(1);
        } else if (answer.equals("<")) {
            currentDate = currentDate.minusMonths(1);
        }
        try {
            var editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(clientId.toString());
            editMessageReplyMarkup.setMessageId(messageId);
            editMessageReplyMarkup.setInlineMessageId(inlineId);
            editMessageReplyMarkup.setReplyMarkup(generateKeyboard(currentDate));
            bot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        cacheService.setCurrentDate(clientId, currentDate);
    }

    private boolean validateDate(String text) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(text, format);
            return !date.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateQuestionAnswer(Question question, String text) {
        if (questionService.isButton(question)) {
            return question.getOptions()
                    .stream().anyMatch(a ->
                            (a.getAnswer().equals(text) || a.getTranslations()
                                    .stream().anyMatch(t -> t.getTranslatedText().equals(text))));
        }
        return text.matches(question.getPattern());
    }
    private LocalDate getDateFromAnswer(String text) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(text, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private BotApiMethod<?> postNewQuestion(String chatId, Long clientId, Question question) {
        String code = cacheService.getCurrentLanguage(clientId);
        cacheService.setCurrentStateQuestion(clientId, question);
        var translation = questionService.getQuestionTranslation(question, code);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(translation);
        if (question.getKey().equals("startDate") || question.getKey().equals("endDate")) {
            var date = cacheService.getCurrentDate(clientId);
            sendMessage.setReplyMarkup(generateKeyboard(date));
        if (question.getKey().equals("startDate")) {
            if (Validation.validateDate(text, LocalDate.now())) {
                cacheService.setCurrentDate(clientId, Validation.getDateFromAnswer(text));
                return true;
            }
        }
        if (question.getKey().equals("endDate")) {
            if (Validation.validateDate(text, cacheService.getCurrentDate(clientId))) {
                cacheService.setCurrentDate(clientId, Validation.getDateFromAnswer(text));
                return true;
            }
        }
        if (questionService.isButton(question)) return showQuestionKeyboard(question, chatId, translation, code);

        return sendMessage;
    }

}
