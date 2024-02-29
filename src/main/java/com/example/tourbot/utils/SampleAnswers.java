package com.example.tourbot.utils;

import java.util.HashMap;
import java.util.Map;

public class SampleAnswers {

    private static final Map<String, Map<String, String>> messages = new HashMap<>();

    static {
        Map<String, String> defaultMessages = new HashMap<>();
        defaultMessages.put("start", "Type /start to start");
        defaultMessages.put("stop", "Current session is closed. Restart session by typing /start");
        defaultMessages.put("activeSession", "You have active session, please first type /stop to restart");
        defaultMessages.put("stopActiveSession", "You don't have active session, please type /start to start");
        defaultMessages.put("languageSetup", "Select bot language \uD83C\uDF0E");
        defaultMessages.put("incorrectAnswer", "❌ Incorrect answer!");
        defaultMessages.put("unrecognizedCommand", "Unrecognized command \uD83E\uDD14! Enter again");
        defaultMessages.put("selectedLanguage", "Selected language:");
        defaultMessages.put("waitForOffers", "\uD83D\uDCAD We are preparing offers for you, please wait");
        defaultMessages.put("loadOffer", "Do you want to load new offers?");
        defaultMessages.put("askForNumber", "\uD83D\uDCF1 Share contact number");

        Map<String, String> ruMessages = new HashMap<>();
        ruMessages.put("start", "Введите /start, чтобы начать");
        ruMessages.put("stop", "Сессия закрыта, введите /start чтобы перезапустить сессию");
        ruMessages.put("activeSession", "У вас уже есть активная сессия. Чтобы перезапустить введите /stop");
        ruMessages.put("stopActiveSession", "У вас нет активного сеанса, введите /start, чтобы начать");
        ruMessages.put("languageSetup", "Выберите язык бота \uD83C\uDF0E");
        ruMessages.put("incorrectAnswer", "❌ Неправильный ответ!");
        ruMessages.put("unrecognizedCommand", "Неверная команда \uD83E\uDD14! Введите запрос заново");
        ruMessages.put("selectedLanguage", "Выбранный язык:");
        ruMessages.put("waitForOffers", "\uD83D\uDCAD Мы готовим предложения для вас, пожалуйста, подождите");
        ruMessages.put("loadOffer", "Хотите загрузить новые предложения?");
        ruMessages.put("askForNumber", "\uD83D\uDCF1 Поделиться номером");

        Map<String, String> azMessages = new HashMap<>();
        azMessages.put("start", "Başlamaq üçün / start yazın");
        azMessages.put("stop", "Sessiyanız bağlandı. Başlamaq üçün /start yazın");
        azMessages.put("activeSession", "Sizin aktiv sessiyanız var, yenidən başlamaq üçün /stop komandasını daxil edin");
        azMessages.put("stopActiveSession", "Sizin aktiv sessiyanız yoxdur, yeni sessiya yaratmaq üçün /start komandasını daxil edin");
        azMessages.put("languageSetup", "Bot dilini seçin \uD83C\uDF0E");
        azMessages.put("incorrectAnswer", "❌ Yanlış cavab!");
        azMessages.put("unrecognizedCommand", "Yanlış komanda \uD83E\uDD14! Yenidən daxil edin");
        azMessages.put("selectedLanguage", "Seçilmiş dil:");
        azMessages.put("waitForOffers", "\uD83D\uDCAD Sizin üçün təkliflər hazırlıyırıq.Zəhmət olmasa gözləyin");
        azMessages.put("loadOffer", " Yeni təkliflər görmək istəyirsinizmi?️");
        azMessages.put("askForNumber", "\uD83D\uDCF1 Əlaqə nömrəsini paylaşın");


        messages.put("default", defaultMessages);
        messages.put("RU", ruMessages);
        messages.put("AZ", azMessages);
    }

    public static String getMessage(String messageKey, String languageCode) {
        Map<String, String> languageMessages = messages.getOrDefault(languageCode, messages.get("default"));
        return languageMessages.getOrDefault(messageKey, "Message not found for key: " + messageKey);
    }



}
