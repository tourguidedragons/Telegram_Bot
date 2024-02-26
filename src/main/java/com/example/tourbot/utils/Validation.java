package com.example.tourbot.utils;

import com.example.tourbot.models.Question;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Validation {

    public static LocalDate getDateFromAnswer(String text) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(text, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    public static boolean validateDate(String text, LocalDate dateCurrent) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(text, format);
            return !date.isBefore(dateCurrent);
        } catch (Exception e) {
            return false;
        }
    }
}
