package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.model.Collection;
import com.poetry.model.Poem;
import com.poetry.service.AppreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppreciationController {

    @Autowired
    private AppreciationService appreciationService;

    private static final Long TEMP_USER_ID = 1L;

    /**
     * 诗词列表 / 搜索
     */
    @GetMapping("/poems")
    public Result<Map<String, Object>> listPoems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Poem> poems = appreciationService.searchPoems(keyword, dynasty, category, tag, page, size);
        return Result.ok(Map.of(
                "content", poems.getContent(),
                "totalElements", poems.getTotalElements(),
                "totalPages", poems.getTotalPages(),
                "currentPage", poems.getNumber()
        ));
    }

    /**
     * 热门诗词
     */
    @GetMapping("/poems/hot")
    public Result<Map<String, Object>> hotPoems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Poem> poems = appreciationService.getHotPoems(page, size);
        return Result.ok(Map.of(
                "content", poems.getContent(),
                "totalElements", poems.getTotalElements()
        ));
    }

    /**
     * 每日一诗
     */
    @GetMapping("/poems/daily")
    public Result<Poem> dailyPoem() {
        Optional<Poem> poem = appreciationService.getDailyPoem();
        return poem.map(Result::ok).orElse(Result.fail("暂无诗词"));
    }

    /**
     * 诗词详情
     */
    @GetMapping("/poems/{id}")
    public Result<Poem> getPoem(@PathVariable Long id) {
        return appreciationService.getPoemById(id)
                .map(Result::ok)
                .orElse(Result.notFound("诗词不存在"));
    }

    // ========== 收藏 ==========

    @PostMapping("/favorites")
    public Result<Map<String, String>> addFavorite(@RequestBody Map<String, Long> params) {
        Long poemId = params.get("poemId");
        appreciationService.addPoemFavorite(TEMP_USER_ID, poemId);
        return Result.ok(Map.of("message", "收藏成功"));
    }

    @DeleteMapping("/favorites/{poemId}")
    public Result<Map<String, String>> removeFavorite(@PathVariable Long poemId) {
        appreciationService.removePoemFavorite(TEMP_USER_ID, poemId);
        return Result.ok(Map.of("message", "已取消收藏"));
    }

    @GetMapping("/favorites")
    public Result<Map<String, Object>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Poem> poems = appreciationService.getFavoritePoems(TEMP_USER_ID, page, size);
        return Result.ok(Map.of(
                "content", poems.getContent(),
                "totalElements", poems.getTotalElements()
        ));
    }

    @GetMapping("/favorites/check")
    public Result<Map<String, Boolean>> checkFavorite(@RequestParam Long poemId) {
        boolean favorited = appreciationService.isFavorited(TEMP_USER_ID, poemId);
        return Result.ok(Map.of("favorited", favorited));
    }
}
