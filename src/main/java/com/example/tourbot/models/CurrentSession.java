package com.example.tourbot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@RedisHash
public class CurrentSession{
    private String UUID;
    @Id
    private Long id;
    private Long chatId;
    private Map<String, String> history;
    private String lang;
    private String question;

//    public CurrentSession() {
//        this.history = new HashMap<>();
//    }
}
