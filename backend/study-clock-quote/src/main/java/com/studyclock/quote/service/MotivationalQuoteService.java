package com.studyclock.quote.service;

import com.studyclock.quote.entity.MotivationalQuote;
import com.studyclock.quote.repository.MotivationalQuoteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MotivationalQuoteService {

    private final MotivationalQuoteRepository repository;

    public MotivationalQuoteService(MotivationalQuoteRepository repository) {
        this.repository = repository;
    }

    public MotivationalQuote getRandom() {
        MotivationalQuote quote = repository.findRandom();
        if (quote == null) {
            quote = new MotivationalQuote();
            quote.setContent("休息一下，继续前进！");
            quote.setCategory("BREAK");
        }
        return quote;
    }

    @PostConstruct
    public void initQuotes() {
        if (repository.count() > 0) return;

        String[][] quotes = {
            {"休息是为了走更长远的路。", "BREAK"},
            {"专注的每一分钟，都在塑造更好的自己。", "FOCUS"},
            {"学习不是填满水桶，而是点燃火焰。", "STUDY"},
            {"千里之行，始于足下。", "FOCUS"},
            {"适当的休息，让大脑更高效。", "BREAK"},
            {"坚持是成功的唯一捷径。", "STUDY"},
            {"每一次专注，都是对未来的投资。", "FOCUS"},
            {"放松一下吧，你已经做得很好了！", "BREAK"},
            {"知识改变命运，学习成就未来。", "STUDY"},
            {"自律带来自由。", "FOCUS"},
        };

        for (String[] q : quotes) {
            MotivationalQuote quote = new MotivationalQuote();
            quote.setContent(q[0]);
            quote.setCategory(q[1]);
            repository.save(quote);
        }
    }
}
