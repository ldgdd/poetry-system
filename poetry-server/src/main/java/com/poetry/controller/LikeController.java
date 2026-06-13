package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.model.Poem;
import com.poetry.model.UserLike;
import com.poetry.model.UserPoem;
import com.poetry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired private UserLikeRepository likeRepo;
    @Autowired private PoemRepository poemRepo;
    @Autowired private UserPoemRepository userPoemRepo;

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(
            @RequestBody Map<String, Long> params,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) return Result.unauthorized("请先登录");

        Long poemId = params.get("poemId");
        Long userPoemId = params.get("userPoemId");
        boolean liked;

        if (poemId != null) {
            var existing = likeRepo.findByUserIdAndPoemId(userId, poemId);
            if (existing.isPresent()) {
                likeRepo.deleteByUserIdAndPoemId(userId, poemId);
                Poem p = poemRepo.findById(poemId).orElse(null);
                if (p != null) { p.setLikeCount(Math.max(0, p.getLikeCount() - 1)); poemRepo.save(p); }
                liked = false;
            } else {
                UserLike like = new UserLike();
                like.setUserId(userId); like.setPoemId(poemId);
                likeRepo.save(like);
                Poem p = poemRepo.findById(poemId).orElse(null);
                if (p != null) { p.setLikeCount(p.getLikeCount() + 1); poemRepo.save(p); }
                liked = true;
            }
        } else if (userPoemId != null) {
            var existing = likeRepo.findByUserIdAndUserPoemId(userId, userPoemId);
            if (existing.isPresent()) {
                likeRepo.deleteByUserIdAndUserPoemId(userId, userPoemId);
                UserPoem up = userPoemRepo.findById(userPoemId).orElse(null);
                if (up != null) { up.setLikeCount(Math.max(0, up.getLikeCount() - 1)); userPoemRepo.save(up); }
                liked = false;
            } else {
                UserLike like = new UserLike();
                like.setUserId(userId); like.setUserPoemId(userPoemId);
                likeRepo.save(like);
                UserPoem up = userPoemRepo.findById(userPoemId).orElse(null);
                if (up != null) { up.setLikeCount(up.getLikeCount() + 1); userPoemRepo.save(up); }
                liked = true;
            }
        } else {
            return Result.fail("请指定 poemId 或 userPoemId");
        }

        return Result.ok(Map.of("liked", liked));
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/check")
    public Result<Map<String, Boolean>> check(
            @RequestParam(required = false) Long poemId,
            @RequestParam(required = false) Long userPoemId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) return Result.ok(Map.of("liked", false));
        boolean liked;
        if (poemId != null) liked = likeRepo.existsByUserIdAndPoemId(userId, poemId);
        else if (userPoemId != null) liked = likeRepo.existsByUserIdAndUserPoemId(userId, userPoemId);
        else liked = false;
        return Result.ok(Map.of("liked", liked));
    }
}
