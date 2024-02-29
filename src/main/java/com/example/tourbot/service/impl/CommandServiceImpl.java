package com.example.tourbot.service.impl;

import com.example.tourbot.bot.Bot;
import com.example.tourbot.exception.ActiveExceptionNotFound;
import com.example.tourbot.exception.CurrentSessionNotFoundException;
import com.example.tourbot.models.Offer;
import com.example.tourbot.models.Question;
import com.example.tourbot.models.Session;
import com.example.tourbot.service.*;
import com.example.tourbot.utils.Button;
import com.example.tourbot.utils.SampleAnswers;
import com.example.tourbot.utils.Validation;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
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
    private final SessionService sessionService;
    private final OfferService offerService;
    private final ImageService imageService;
    private final Bot bot;
    private final ImageService imageService;

    public CommandServiceImpl(QuestionService questionService, OptionService optionService, CacheService cacheService, SessionService sessionService, OfferService offerService, ImageService imageService, @Lazy Bot bot) {
        this.questionService = questionService;
        this.optionService = optionService;
        this.cacheService = cacheService;
        this.sessionService = sessionService;
        this.offerService = offerService;
        this.imageService = imageService;
        this.bot = bot;
        this.imageService = imageService;
    }

    @Override
    public BotApiMethod<?> validateCallBackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = (Message) callbackQuery.getMessage();
        Integer messageId = message.getMessageId();

        long clientId = callbackQuery.getFrom().getId();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String answer = callbackQuery.getData();
        String questionKey = cacheService.getCurrentStateQuestion(clientId);
        Question question = questionService.getQuestionByKey(questionKey);

        if (answer.equals("next")) {
            sendOffers(clientId);
            return null;
        }
        if (question == null || question.getKey().equals("complete") || question.getKey().equals("waitForOffer"))
            return null;
        if (answer.equals("<") || answer.equals(">")) {
            handleCalendar(messageId, update.getCallbackQuery().getInlineMessageId(), clientId, answer);
            return null;
        }


        if (question.getKey().equals("language")) {
            cacheService.setSelectedLanguage(clientId, answer);
        }
        if (!validateQuestionAnswer(question, answer, clientId)) {
            handleIncorrectAnswer(chatId, messageId, clientId);
            return postQuestion(chatId, clientId, questionService.getQuestionByKey(questionKey));
        }
        try {
            bot.execute(new DeleteMessage(chatId, messageId));
            bot.execute(new SendMessage(chatId, answer));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        cacheService.saveUserAnswer(clientId, question.getKey(), answer);
        return postQuestion(chatId, clientId, questionService.getNextQuestion(question, answer));
    }

    @Override
    public BotApiMethod<?> validateCommands(Message message) {
        String chatId = message.getChatId().toString();
        long clientId = message.getFrom().getId();
        String text = message.getText();

        if (getActiveSession(clientId)) {
            var currentLang = cacheService.getCurrentLanguage(clientId);
            if (text.equals("/start")) {
                return new SendMessage(chatId, SampleAnswers.getMessage("activeSession", currentLang));
            } else if (text.equals("/stop")) {
                cacheService.disableActiveSession(clientId);
                return new SendMessage(chatId, SampleAnswers.getMessage("stop", currentLang));
            }
        } else {
            if (text.equals("/start")) {
                return startSession(chatId, clientId, message);
            } else if (text.equals("/stop")) {
                return new SendMessage(chatId, SampleAnswers.getMessage("stopActiveSession", "EN"));
            }
        }
        return new SendMessage(chatId, "Unrecognized command");
    }

    @Override
    public BotApiMethod<?> validateReplyMessage(Message message) {
        return null;
    }

    @Override
    public BotApiMethod<?> validateMessage(Message message) {
        Long clientId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        String answer = message.getText();
        String currentKey = cacheService.getCurrentStateQuestion(clientId);
        Question question = questionService.getQuestionByKey(currentKey);

        if (!validateQuestionAnswer(question, answer, clientId)) {
            handleIncorrectAnswer(chatId, message.getMessageId(), clientId);
            return postQuestion(chatId, clientId, questionService.getQuestionByKey(question.getKey()));
        }
        if (question == null || question.getKey().equals("complete")
                || question.getKey().equals("waitForOffer") || question.getKey().equals("phone")) return null;

        return postQuestion(chatId, clientId, questionService.getNextQuestion(question, answer));
    }

    @Override
    public BotApiMethod<?> validateContact(Message message) {
        Long clientId = message.getFrom().getId();
        String currentKey = cacheService.getCurrentStateQuestion(clientId);
        Question question = questionService.getQuestionByKey(currentKey);
        String answer = message.getContact().getPhoneNumber();
        cacheService.saveUserAnswer(clientId, currentKey, answer);
        return postQuestion(message.getChatId().toString(), clientId, questionService.getNextQuestion(question, answer));
    }

    public Boolean getActiveSession(Long clientId) {
        try {
            return cacheService.find(clientId) != null;
        } catch (CurrentSessionNotFoundException exception) {
            log.error("No active session found for clientId: {}", clientId);
            return false;
        }
    }

    private BotApiMethod<?> startSession(String chatId, long clientId, Message message) {
        var question = questionService.getQuestionByKey("language");
        cacheService.createSession(message);
        cacheService.setCurrentStateQuestion(clientId, question);
        return showQuestionKeyboard(question, chatId, SampleAnswers.getMessage("languageSetup", "EN"));
    }

    private void handleIncorrectAnswer(String chatId, Integer messageId, Long clientId) {
        try {
            String currentLang = cacheService.getCurrentLanguage(clientId);
            bot.execute(new SendMessage(chatId, SampleAnswers.getMessage("incorrectAnswer", currentLang)));
            bot.execute(new DeleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private BotApiMethod<?> postQuestion(String chatId, Long clientId, Question question) {
        String code = cacheService.getCurrentLanguage(clientId);
        cacheService.setCurrentStateQuestion(clientId, question);
        var translation = questionService.getQuestionTranslation(question, code);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(translation);
        if (question.getKey().equals("startDate") || question.getKey().equals("endDate")) {
            var date = cacheService.getCurrentDate(clientId);
            sendMessage.setReplyMarkup(generateKeyboard(date));
        }
        if (question.getKey().equals("complete")) {
           cacheService.endCurrentSession(clientId);
        }
        if (question.getKey().equals("waitForOffer")) {
        Map<String, String> buttons = new LinkedHashMap<>();
        buttons.put("picture2", "picture1");
        imageService.sendPhotoToChat(clientId, "ooo.png", "text", Button.maker(buttons));
            return null;
        }
        if (question.getKey().equals("phone")) {
            sendRequestContactButton(chatId, clientId);
            return null;
        }
        if (questionService.isButton(question)) return showQuestionKeyboard(question, chatId, translation, code);
        return sendMessage;
    }

    private void sendRequestContactButton(String chatId, Long clientId) {
        KeyboardButton button = new KeyboardButton();
        String lang = cacheService.getCurrentLanguage(clientId);
        button.setText(SampleAnswers.getMessage("askForNumber", lang));
        button.setRequestContact(true);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        var currentLang = cacheService.getCurrentLanguage(clientId);
        var currentQuestion = cacheService.getCurrentStateQuestion(clientId);
        var translation = questionService.getQuestionTranslation(questionService.getQuestionByKey(currentQuestion), currentLang);
        SendMessage message = new SendMessage(chatId, translation);
        message.setReplyMarkup(keyboardMarkup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
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

    private BotApiMethod<?> sendMessage(String chatId, String textMessage, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (textMessage != null && !textMessage.isEmpty()) message.setText(textMessage);
        else return null;
        if (keyboard != null) message.setReplyMarkup(keyboard);
        return message;
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
    }

    private boolean validateQuestionAnswer(Question question, String text, Long clientId) {
        if (questionService.isButton(question)) {
            return question.getOptions()
                    .stream().anyMatch(a ->
                            (a.getAnswer().equals(text) || a.getTranslations()
                                    .stream().anyMatch(t -> t.getTranslatedText().equals(text))));
        }
        if (question.getPattern() != null) return text.matches(question.getPattern());

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


        return false;
    }

    @Override
    public Boolean canHandleUpdate(Update update) {
        Long clientId = null;
        if (update.hasCallbackQuery()) {
            clientId = update.getCallbackQuery().getFrom().getId();
        } else if (update.hasMessage()) {
            if (!update.getMessage().hasText()) return false;
            if (update.getMessage().getText().startsWith("/")) return true;
            clientId = update.getMessage().getFrom().getId();
        } else {
            return false;
        }
        if (!getActiveSession(clientId)) {
            try {
                bot.execute(new SendMessage(clientId.toString(),
                        SampleAnswers.getMessage("stopActiveSession", "EN")));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return true;
    }


    public void sendOffers(Long clientId) throws ActiveExceptionNotFound {
        Session session = sessionService.findByClientId(clientId)
                .stream().filter(Session::getIsActive).findFirst()
                .orElseThrow(() -> new ActiveExceptionNotFound("CurrentSession not found with clientId: " + clientId));

        cacheService.getPendingOffers(session.getUuid()).forEach(c -> {
            Offer offer = Offer.builder()
                    .content(c.getContent())
                    .uuid(c.getUuid())
                    .image(c.getImage())
                    .isSent(false).session(session).build();

            validateOffers(offer);
            offerService.save(offer);
            cacheService.clearPendingOffers(c);
        });
    }

    public void validateOffers(Offer offer) {
        var count = cacheService.pendingCount(offer.getUuid());
        if (count < 5) {
            Map<String, String> buttons = new LinkedHashMap<>();
            buttons.put(offer.getMessageId().toString(), "Click to choose");
            var messageId = imageService.sendPhotoToChat(offer.getSession().getClientId(), offer.getImage(), offer.getContent(), Button.maker(buttons));
            offer.setMessageId(messageId);

        } else if (count == 5) {
            Map<String, String> buttons = new LinkedHashMap<>();
            buttons.put("next", "next");
            var currentLang = cacheService.getCurrentLanguage(offer.getSession().getClientId());

            SendMessage message = SendMessage.builder()
                    .chatId(offer.getSession().getChatId())
                    .text(SampleAnswers.getMessage("loadOffer", currentLang))
                    .replyMarkup(Button.maker(buttons))
                    .build();
            try {
                bot.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }


    }

}
