package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.model.Comment;
import com.poetry.model.Poem;
import com.poetry.model.UserPoem;
import com.poetry.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private PoemRepository poemRepo;
    @Autowired private UserPoemRepository userPoemRepo;
    @Autowired private CommentRepository commentRepo;
    @Autowired private UserRepository userRepository;

    /**
     * 校验管理员身份
     */
    private boolean isAdmin(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return false;
        return userRepository.findById(userId)
                .map(u -> "ADMIN".equals(u.getRole()))
                .orElse(false);
    }

    private <T> Result<T> requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.forbidden("需要管理员权限");
        return null;
    }

    // ==================== 诗词录入 ====================

    /**
     * POST /api/admin/poems — 管理员录入新诗词
     */
    @PostMapping("/poems")
    public Result<Poem> addPoem(@RequestBody Poem poem, HttpServletRequest request) {
        Result<Poem> check = requireAdmin(request);
        if (check != null) return check;

        poem.setId(null); // 新记录
        poem.setViewCount(0L);
        poem.setLikeCount(0L);
        poem.setStatus(1);
        Poem saved = poemRepo.save(poem);
        return Result.ok("录入成功", saved);
    }

    /**
     * DELETE /api/admin/poems/{id} — 管理员删除诗词
     */
    @DeleteMapping("/poems/{id}")
    public Result<Void> deletePoem(@PathVariable Long id, HttpServletRequest request) {
        Result<Void> check = requireAdmin(request);
        if (check != null) return check;

        Poem p = poemRepo.findById(id).orElse(null);
        if (p == null) return Result.notFound("诗词不存在");
        p.setStatus(0);
        poemRepo.save(p);
        return Result.okMsg("已删除（隐藏）");
    }

    // ==================== 评论管理 ====================

    /**
     * DELETE /api/admin/comments/{id} — 管理员删除任意评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Result<Void> check = requireAdmin(request);
        if (check != null) return check;

        Comment c = commentRepo.findById(id).orElse(null);
        if (c == null) return Result.notFound("评论不存在");
        c.setStatus(-1);
        commentRepo.save(c);
        return Result.okMsg("评论已删除");
    }

    // ==================== 用户作品管理 ====================

    /**
     * GET /api/admin/user-poems — 查看所有用户作品（含已删除）
     */
    @GetMapping("/user-poems")
    public Result<?> listAllUserPoems(HttpServletRequest request) {
        Result<?> check = requireAdmin(request);
        if (check != null) return check;
        return Result.ok(userPoemRepo.findAll());
    }

    /**
     * DELETE /api/admin/user-poems/{id} — 管理员删除用户作品
     */
    @DeleteMapping("/user-poems/{id}")
    public Result<Void> deleteUserPoem(@PathVariable Long id, HttpServletRequest request) {
        Result<Void> check = requireAdmin(request);
        if (check != null) return check;

        UserPoem p = userPoemRepo.findById(id).orElse(null);
        if (p == null) return Result.notFound("作品不存在");
        p.setStatus(-1);
        userPoemRepo.save(p);
        return Result.okMsg("作品已删除");
    }

    // ==================== 数据统计 ====================

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(HttpServletRequest request) {
        Result<Map<String, Object>> check = requireAdmin(request);
        if (check != null) return check;
        return Result.ok(Map.of(
                "poemCount", poemRepo.count(),
                "userPoemCount", userPoemRepo.count(),
                "userCount", userRepository.count()
        ));
    }
}
