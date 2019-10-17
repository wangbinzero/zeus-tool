package com.tools.zeus.controller;

import com.tools.zeus.bot.KlineBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private KlineBot klineBot;

    @GetMapping("/kline")
    public String kline() throws InterruptedException {
        klineBot.klineWork();
        return "Kline";
    }
}
