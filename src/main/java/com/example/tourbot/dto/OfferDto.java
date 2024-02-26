package com.example.tourbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto implements Serializable {
    private Long clientId;
    private String name;
    private String surname;
    private String username;
    private String phone;
    private Integer offerId;
}
