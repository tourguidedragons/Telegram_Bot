package com.example.tourbot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandService {
    BotApiMethod<?> validateCallBackQuery(Update update);
    BotApiMethod<?> validateCommands(Message message);
    BotApiMethod<?> validateReplyMessage(Message message);
    BotApiMethod<?> validateMessage(Message message);
    BotApiMethod<?> validateContact(Message message);
   Boolean canHandleUpdate(Update update);


}