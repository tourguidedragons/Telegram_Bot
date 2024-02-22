package com.example.tourbot.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sessions")
public class Session implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer clientId;

    private Long chatId;

    private LocalDateTime expireDate;

    private String phone;

    private Boolean isActive;

    @ElementCollection
    @CollectionTable(name="sessionHistory")
    @MapKeyColumn(name="qkey")
    private Map<String, String> sessionHistory;


    @OneToMany(mappedBy = "session",
            fetch = FetchType.EAGER)
    private List<Offer> travelPackages;


}
