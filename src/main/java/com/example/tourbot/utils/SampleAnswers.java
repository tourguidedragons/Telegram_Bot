package com.example.tourbot.utils;

import java.util.HashMap;
import java.util.Map;

public class SampleAnswers {

    private static final Map<String, Map<String, String>> messages = new HashMap<>();

    static {
        Map<String, String> enMessages = new HashMap<>();
        enMessages.put("start", "Type /start to start");
        enMessages.put("stop", "Current session is closed. Restart session by typing /start");
        enMessages.put("activeSession", "You have active session, please first type /stop to restart");
        enMessages.put("stopActiveSession", "You don't have active session, please type /start to start");
        enMessages.put("languageSetup", "Select bot language");
        enMessages.put("incorrectAnswer", "Incorrect answer!");
        enMessages.put("unrecognizedCommand", "Unrecognized command! Enter again");
        enMessages.put("selectedLanguage", "Selected language:");

        Map<String, String> ruMessages = new HashMap<>();
        ruMessages.put("start", "Введите /start, чтобы начать");
        ruMessages.put("stop", "Сессия закрыта, введите /start чтобы перезапустить сессию");
        ruMessages.put("activeSession", "У вас уже есть активная сессия. Чтобы перезапустить введите /stop");
        ruMessages.put("stopActiveSession", "У вас нет активного сеанса, введите /start, чтобы начать");
        ruMessages.put("languageSetup", "Выберите язык бота");
        ruMessages.put("incorrectAnswer", "Неправильный ответ!");
        ruMessages.put("unrecognizedCommand", "Неверная команда! Введите запрос заново");
        ruMessages.put("selectedLanguage", "Выбранный язык:");


        Map<String, String> defaultMessages = new HashMap<>();
        defaultMessages.put("start", "Başlamaq üçün / start yazın");
        defaultMessages.put("stop", "Sessiyanız bağlandı. Başlamaq üçün /start yazın");
        defaultMessages.put("activeSession", "Sizin aktiv sessiyanız var, yenidən başlamaq üçün /stop komandasını daxil edin");
        defaultMessages.put("stopActiveSession", "Sizin aktiv sessiyanız yoxdur, yeni sessiya yaratmaq üçün /start komandasını daxil edin");
        defaultMessages.put("languageSetup", "Bot dilini seçin");
        defaultMessages.put("incorrectAnswer", "Yanlış cavab!");
        defaultMessages.put("unrecognizedCommand", "Yanlış komanda! Yenidən daxil edin");
        defaultMessages.put("selectedLanguage", "Seçilmiş dil:");

        messages.put("EN", enMessages);
        messages.put("RU", ruMessages);
        messages.put("default", defaultMessages);
    }

    public static String getMessage(String messageKey, String languageCode) {
        Map<String, String> languageMessages = messages.getOrDefault(languageCode, messages.get("default"));
        return languageMessages.getOrDefault(messageKey, "Message not found for key: " + messageKey);
    }



}
