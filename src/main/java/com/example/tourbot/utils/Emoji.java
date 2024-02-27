package com.example.tourbot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emoji {
    INCORRECT_ANSWER(EmojiParser.parseToUnicode(":cross_mark:")),
    ACTIVE_SESSION(EmojiParser.parseToUnicode(":clock8:")),
    LANGUAGE_SETUP(EmojiParser.parseToUnicode(":clock3:")),
    TIME_IN_WAY(EmojiParser.parseToUnicode(":alarm_clock:"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
