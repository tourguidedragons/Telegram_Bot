package com.example.tourbot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class ImageService extends DefaultAbsSender {
    public ImageService(@Value("${telegrambot.botToken}") String telegramBotToken) {
        super(new DefaultBotOptions(), telegramBotToken);
    }

    public int sendPhotoToChat(Long chatId, byte[] image, String caption, ReplyKeyboard keyboard) {
        try {
            InputFile inputFile = new InputFile();
            inputFile.setMedia(new File(photoFilePath));

            File imageConverted = imageFromBytes(image);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            InputFile inputFile = new InputFile();
            inputFile.setMedia(imageConverted);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setCaption(caption);

            sendPhoto.setReplyMarkup(keyboard);
            Message message = this.execute(sendPhoto);

            return message.getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public File imageFromBytes(byte[] image) {
        try {
            FileUtils.writeByteArrayToFile(new File("image.jpg"), image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new File("image.jpg");

    }


}