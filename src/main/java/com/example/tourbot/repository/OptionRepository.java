package com.example.tourbot.repository;

import com.example.tourbot.models.Option;
import com.example.tourbot.models.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Integer> {

}
