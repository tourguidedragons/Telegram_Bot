package com.example.tourbot.bot;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;



    @RequiredArgsConstructor
    public class Bot extends TelegramWebhookBot {

        private String webHookPath;
        private String botUserName;
        private String botToken;

        @Override
        public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
            return null;
        }

        @Override
        public String getBotToken() {
            return botToken;
        }

        @Override
        public String getBotUsername() {
            return botUserName;
        }


        @Override
        public String getBotPath() {
            return webHookPath;
        }

        @Override
        public void onRegister() {
            super.onRegister();
        }

        public void setBotToken(String botToken) {
            this.botToken = botToken;
        }

        public void setBotUsername(String botUsername) {
            this.botUserName = botUsername;
        }

        public void setWebhookPath(String webhookPath) {
            this.webHookPath = webhookPath;
        }
    }
