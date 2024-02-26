package com.example.tourbot.dto;

import com.example.tourbot.models.CurrentSession;

import java.util.Map;

public class SessionDto {

    private Map<String, String> history;

    public SessionDto(CurrentSession session){
        this.history = session.getHistory();
    }
}
