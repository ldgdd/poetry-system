package com.poetry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcrosticService {

    @Autowired
    private DeepSeekService deepSeekService;

    /**
     * 藏头诗生成
     * @param headWord 藏头字（如 "春天花开"）
     * @param style    风格（五言/七言，可选）
     */
    public String generate(String headWord, String style) {
        String styleHint = (style == null || style.isEmpty()) ? "七言绝句" : style;
        String userPrompt = String.format(
            "请写一首%s，藏头为「%s」。要求：\n" +
            "1. 每句第一个字连起来正好是「%s」\n" +
            "2. 意境优美，语言古典雅致\n" +
            "3. 符合格律要求\n" +
            "4. 在诗句后面用括号标注每句数量和格式是否正确\n" +
            "请直接输出诗句，不要输出额外说明。",
            styleHint, headWord, headWord);

        return deepSeekService.chat(
            "你是一位精通中国古典诗词的诗人，擅长创作藏头诗。请严格按照用户要求创作，只输出诗词本身。",
            userPrompt);
    }
}
