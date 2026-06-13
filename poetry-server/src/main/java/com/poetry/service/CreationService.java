package com.poetry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreationService {

    @Autowired
    private DeepSeekService deepSeekService;

    /**
     * 诗词创作
     * @param theme  主题/关键词
     * @param type   类型（五言/七言/宋词/元曲）
     * @param mood   意境（豪放/婉约/田园/边塞等）
     */
    public String create(String theme, String type, String mood) {
        String typeHint = (type == null || type.isEmpty()) ? "七言绝句" : type;
        String moodHint = (mood == null || mood.isEmpty()) ? "意境深远" : mood;

        String userPrompt = String.format(
            "请创作一首古典诗词：\n" +
            "- 主题/关键词：%s\n" +
            "- 类型：%s\n" +
            "- 风格意境：%s\n" +
            "要求：语言优美、符合格律、意境深远。请直接输出诗词，在诗后简要说明创作思路。",
            theme, typeHint, moodHint);

        return deepSeekService.chat(
            "你是一位才华横溢的中国古典诗词作家，精通唐诗、宋词、元曲等各种体裁。请严格按用户要求创作。",
            userPrompt);
    }
}
