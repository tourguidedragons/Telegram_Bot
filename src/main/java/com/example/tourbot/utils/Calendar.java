package com.example.tourbot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Calendar {

    public static final String IGNORE = "ignore!@#$%^&";

    public static final String[] WD = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};

    public static InlineKeyboardMarkup generateKeyboard(LocalDate date) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        if (date == null) return null;

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // row - Month and Year
        List<InlineKeyboardButton> headerRow = new ArrayList<>();
        headerRow.add(createButton(IGNORE, date.format(DateTimeFormatter.ofPattern("MMM yyyy"))));
        keyboard.add(headerRow);

        // row - Days of the week
        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        for (String day : WD) {
            daysOfWeekRow.add(createButton(IGNORE, day));
        }
        keyboard.add(daysOfWeekRow);

        LocalDate firstDay = date.withDayOfMonth(1);

        int shift = firstDay.getDayOfWeek().getValue() - 1;
        int daysInMonth = firstDay.lengthOfMonth();
        int rows = ((daysInMonth + shift) % 7 > 0 ? 1 : 0) + (daysInMonth + shift) / 7;
        for (int i = 0; i < rows; i++) {
            keyboard.add(buildRow(firstDay, shift));
            firstDay = firstDay.plusDays(7 - shift);
            shift = 0;
        }

        List<InlineKeyboardButton> controlsRow = new ArrayList<>();
        if (date.getMonthValue() == LocalDate.now().getMonthValue()) {
            controlsRow.add(createButton(">", ">"));
        } else {
            controlsRow.add(createButton("<", "<"));
            controlsRow.add(createButton(">", ">"));
        }
        keyboard.add(controlsRow);
        markup.setKeyboard(keyboard);
        return markup;
    }

    private static InlineKeyboardButton createButton(String callBack, String text) {
        InlineKeyboardButton inlineBtn = new InlineKeyboardButton();
        inlineBtn.setCallbackData(callBack);
        inlineBtn.setText(text);
        return inlineBtn;
    }


    private static List<InlineKeyboardButton> buildRow(LocalDate date, int shift) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        int day = date.getDayOfMonth();


        LocalDate callbackDate = date;
        for (int j = 0; j < shift; j++) {
            row.add(createButton(IGNORE, " "));
        }
        for (int j = shift; j < 7; j++) {
            if (day <= (date.lengthOfMonth())) {
                if (date.getYear() == LocalDate.now().getYear() && date.getMonthValue() == LocalDate.now().getMonthValue()) {
                    if (day == LocalDate.now().getDayOfMonth()) {
                        row.add(createButton(callbackDate.toString(), "\uD83D\uDCC5"));
                    } else if (day < LocalDate.now().getDayOfMonth()) {
                        row.add(createButton(callbackDate.toString(), "\uD83D\uDEAB"));
                    } else {
                        row.add(createButton(callbackDate.toString(), Integer.toString(day)));
                    }
                } else {
                    row.add(createButton(callbackDate.toString(), Integer.toString(day)));
                }
                day++;
                callbackDate = callbackDate.plusDays(1);
            } else {
                row.add(createButton(IGNORE, " "));
            }
        }

        return row;
    }
}
