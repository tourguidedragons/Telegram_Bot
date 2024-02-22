package com.example.tourbot.service.impl;

import com.example.tourbot.service.CommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommandServiceImpl implements CommandService {

    public BotApiMethod<?> validateCallBackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();

        Message message = (Message) callbackQuery.getMessage();
        Integer messageId = message.getMessageId();

        long clientId = callbackQuery.getFrom().getId();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String answer = callbackQuery.getData();
        return sendMessage(chatId, answer, null);

    }


    public BotApiMethod<?> validateCommands(Message message) {
        String chatId = message.getChatId().toString();
        long clientId = message.getFrom().getId();
        if (message.getText().equals("/start")) {
            return sendMessage(chatId, "Session started", null);
        } else if (message.getText().equals("/stop")) {
            return sendMessage(chatId, "Session stopped", null);

        }
        return null;

    }


    public BotApiMethod<?> validateReplyMessage(Message message) {
        String chatId = message.getChatId().toString();
        long clientId = message.getFrom().getId();
        return null;
    }

    public BotApiMethod<?> validateMessage(Message message) {
        Long clientId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        String answer = message.getText();
        return null;
    }

    private SendMessage sendMessage(String chatId, String textMessage, ReplyKeyboard keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (textMessage != null && !textMessage.isEmpty()) message.setText(textMessage);
        else return null;
        if (keyboard != null) message.setReplyMarkup(keyboard);
        return message;
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





}
