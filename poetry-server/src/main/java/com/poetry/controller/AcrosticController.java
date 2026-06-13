package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.service.AcrosticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/acrostic")
public class AcrosticController {

    @Autowired
    private AcrosticService acrosticService;

    @PostMapping("/generate")
    public Result<Map<String, String>> generate(@RequestBody Map<String, String> params) {
        String headWord = params.getOrDefault("headWord", "");
        if (headWord.isBlank()) {
            return Result.fail(400, "请输入藏头字");
        }
        String style = params.getOrDefault("style", "七言绝句");
        String poem = acrosticService.generate(headWord, style);
        return Result.ok(Map.of("poem", poem));
    }
}
