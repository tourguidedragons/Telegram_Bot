package com.example.tourbot.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class Button {

    public static InlineKeyboardMarkup maker(Map<String, String> buttons) {
        InlineKeyboardMarkup inlineKeyboardAbout = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        setButtons(buttons, rowInLine);
        rowsInLine.add(rowInLine);
        inlineKeyboardAbout.setKeyboard(rowsInLine);

        return inlineKeyboardAbout;
    }

    public static void setButtons(Map<String, String> buttons, List<InlineKeyboardButton> rowInLine) {
        try {
            for (Map.Entry<String, String> item : buttons.entrySet()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(item.getValue());
                button.setCallbackData(item.getKey());
                rowInLine.add(button);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
