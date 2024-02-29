package com.example.tourbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash
public class OfferDto {
    private Integer id;
    private byte[] image;
    private String content;
    private Boolean isSent;
    private UUID uuid;
}
