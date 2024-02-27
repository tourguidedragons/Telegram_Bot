package com.example.tourbot.utils;

import com.example.tourbot.models.Session;
import com.example.tourbot.repository.SessionRepository;
import com.example.tourbot.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
@RequiredArgsConstructor
public class Scheduler {
    private SessionRepository repository;
    private CacheService service;



    @Scheduled(fixedRateString =  "${expiration.check.millisecond}")
    public void expireRequests() {
        try {
            repository.expireSessions();
        }catch (Exception e){
        }
    }
}
