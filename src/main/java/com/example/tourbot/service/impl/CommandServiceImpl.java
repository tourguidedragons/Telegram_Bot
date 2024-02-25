package com.example.tourbot.service.impl;

import com.example.tourbot.bot.Bot;
import com.example.tourbot.exception.CurrentSessionNotFoundException;
import com.example.tourbot.models.Question;
import com.example.tourbot.service.CacheService;
import com.example.tourbot.service.CommandService;
import com.example.tourbot.service.QuestionService;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static  com.example.tourbot.utils.Calendar.generateKeyboard;

@Slf4j
@Service
public class CommandServiceImpl implements CommandService {
    private final QuestionService questionService;
    private final CacheService cacheService;
    private final Bot bot;

    public CommandServiceImpl(QuestionService questionService, CacheService cacheService, @Lazy Bot bot) {
        this.questionService = questionService;
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
            handleCalendar(messageId, update.getCallbackQuery().getInlineMessageId(), chatId, clientId, answer);
            return null;
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

    private BotApiMethod<?> postNewQuestion(String chatId, Long clientId, Question question) {
        String code = cacheService.getCurrentLanguage(clientId);
        cacheService.setCurrentStateQuestion(clientId, question);
        var translation = questionService.getQuestionTranslation(question, code);
//        if (question.getKey().equals("language")) {
//            return showQuestionKeyboard(question, chatId, translation);
//        }

        if (question.getKey().equals("startDate") || question.getKey().equals("endDate")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(translation);
            sendMessage.setReplyMarkup(generateKeyboard(LocalDate.now()));
        }
        return showQuestionKeyboard(question, chatId, translation);

    }



    public BotApiMethod<?> validateCommands(Message message) {
        String chatId = message.getChatId().toString();
        long clientId = message.getFrom().getId();
        if (message.getText().equals("/start")) {

            if (getActiveSession(clientId)) {
                var currentLang = cacheService.getCurrentLanguage(clientId);
                return new SendMessage(chatId, "");
            } else {
                cacheService.createSession(message);
                var question = questionService.getQuestionByKey("language");
                if (question.getKey().equals("language")) {
                    cacheService.setCurrentStateQuestion(clientId, question);
                    return showQuestionKeyboard(question, chatId, "Dil secin");
                }
            }

        } else if (message.getText().equals("/stop")) {
            String lang = cacheService.getCurrentLanguage(clientId);
            cacheService.stopSession(message);
            return new SendMessage(chatId, "stopped");
        }
        return new SendMessage(chatId,"unrecognized");

    }

    public Boolean getActiveSession(Long clientId) {
        try {
            return cacheService.find(clientId) != null;
        }
        catch (CurrentSessionNotFoundException exception){
            return false;
        }
    }
    private void handleCalendar(Integer messageId, String inlineId, String chatId, Long clientId, String answer) {

        //cache month !!!!!
        LocalDate currentDate = LocalDate.of(2024, 2, 22);
        if (answer.equals(">")) {
            currentDate = currentDate.plusMonths(1);
        } else if (answer.equals("<")) {
            currentDate = currentDate.minusMonths(1);
        }
        try {
            var em = new EditMessageReplyMarkup();
            em.setChatId(chatId);
            em.setMessageId(messageId);
            em.setInlineMessageId(inlineId);
            em.setReplyMarkup(generateKeyboard(currentDate));
            bot.execute(em);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public BotApiMethod<?> validateReplyMessage(Message message) {
        String chatId = message.getChatId().toString();
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
        return postNewQuestion(chatId, clientId, questionService.getNextQuestion(question, answer));
    }

    private SendMessage sendMessage(String chatId, String textMessage, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (textMessage != null && !textMessage.isEmpty()) message.setText(textMessage);
        else return null;
        if (keyboard != null) message.setReplyMarkup(keyboard);
        return message;
    }

    public BotApiMethod<?> showQuestionKeyboard(Question question, String chatId, String text) {
        Map<String, String> buttons = new LinkedHashMap<>();
        question.getOptions().forEach(option -> buttons.put(option.getAnswer(), option.getAnswer()));
        return sendMessage(chatId, text, maker(buttons));
    }
    public static InlineKeyboardMarkup maker(Map<String, String> buttons) {
        InlineKeyboardMarkup inlineKeyboardAbout = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        setButtons(buttons, rowInLine);
        rowsInLine.add(rowInLine);
        inlineKeyboardAbout.setKeyboard(rowsInLine);

        return inlineKeyboardAbout;
    }

    private static void setButtons(Map<String, String> buttons, List<InlineKeyboardButton> rowInLine) {
        try {
            for (Map.Entry<String, String> item : buttons.entrySet()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(item.getValue());
                button.setCallbackData(item.getKey());
                rowInLine.add(button);
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
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



}
