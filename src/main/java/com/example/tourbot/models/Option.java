package com.example.tourbot.models;

import com.example.tourbot.enums.OptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "options")
public class Option implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private UUID uuid;
    private String answer;
    @Enumerated(EnumType.STRING)
    private OptionType optionType;

    @ManyToOne()
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "next_question_id", referencedColumnName = "question_id")
    private Question nextQuestion;

    @OneToMany(mappedBy = "option",
            cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Translation> translations;

    @Override
    public String toString() {
        return "Option{}:";
    }
}
