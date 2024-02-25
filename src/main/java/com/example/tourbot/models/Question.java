package com.example.tourbot.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer id;

    @Column(name = "content")
    private String content;

    @Column(name = "question_key")
    private String key;

    @Column(name = "pattern")
    private String pattern;


    @OneToMany(mappedBy = "question",
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Translation> translations;

    @OneToMany(mappedBy = "question",
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Option> options;


    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", key='" + key + '\'' +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}