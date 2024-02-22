package com.example.tourbot.config;

import com.example.tourbot.bot.Bot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("telegrambot")
public class BotConfig {

    private String webHookPath;
    private String userName;
    private String botToken;
    @Bean
    public Bot BotConfiguration() {

        Bot bot = new Bot();
        bot.setBotUsername(userName);
        bot.setBotToken(botToken);
        bot.setWebhookPath(webHookPath);

        return bot;
    }
}
