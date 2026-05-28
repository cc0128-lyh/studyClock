package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.quote.entity.MotivationalQuote;
import com.studyclock.quote.service.MotivationalQuoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final MotivationalQuoteService service;

    public QuoteController(MotivationalQuoteService service) {
        this.service = service;
    }

    @GetMapping("/random")
    public ApiResult<MotivationalQuote> random() {
        return ApiResult.success(service.getRandom());
    }
}
