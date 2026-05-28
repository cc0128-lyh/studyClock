package com.studyclock.quote.repository;

import com.studyclock.quote.entity.MotivationalQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MotivationalQuoteRepository extends JpaRepository<MotivationalQuote, Long> {

    @Query(value = "SELECT * FROM motivational_quotes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    MotivationalQuote findRandom();
}
