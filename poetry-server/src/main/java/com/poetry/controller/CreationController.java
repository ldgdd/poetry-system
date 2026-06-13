package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.service.CreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/creation")
public class CreationController {

    @Autowired
    private CreationService creationService;

    @PostMapping("/generate")
    public Result<Map<String, String>> generate(@RequestBody Map<String, String> params) {
        String theme = params.getOrDefault("theme", "");
        if (theme.isBlank()) {
            return Result.fail(400, "请输入主题");
        }
        String type = params.getOrDefault("type", "七言绝句");
        String mood = params.getOrDefault("mood", "意境深远");
        String poem = creationService.create(theme, type, mood);
        return Result.ok(Map.of("poem", poem));
    }
}
