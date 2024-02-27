package com.example.tourbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Service
@Slf4j
public class ImageService extends DefaultAbsSender {
    public ImageService(@Value("${telegrambot.botToken}") String telegramBotToken) {
        super(new DefaultBotOptions(), telegramBotToken);
    }

    public void sendPhotoToChat(Long chatId, String photoFilePath, String caption, ReplyKeyboard keyboard) {
        try {
            InputFile inputFile = new InputFile();
            inputFile.setMedia(new File(photoFilePath));

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setCaption(caption);
            sendPhoto.setReplyMarkup(keyboard);
            this.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}