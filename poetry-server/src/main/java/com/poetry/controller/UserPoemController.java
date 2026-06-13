package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.model.User;
import com.poetry.model.UserPoem;
import com.poetry.repository.UserPoemRepository;
import com.poetry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-poems")
public class UserPoemController {

    @Autowired private UserPoemRepository userPoemRepo;
    @Autowired private UserRepository userRepository;

    /**
     * 论坛列表（所有已发布作品，含作者名）
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserPoem> poems = userPoemRepo.findByStatus(1,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Long> userIds = poems.stream().map(UserPoem::getUserId).distinct().toList();
        Map<Long, String> nameMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));

        List<Map<String, Object>> enriched = poems.getContent().stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId());
            m.put("userId", p.getUserId());
            m.put("authorName", nameMap.getOrDefault(p.getUserId(), "用户" + p.getUserId()));
            m.put("title", p.getTitle());
            m.put("content", p.getContent());
            m.put("description", p.getDescription());
            m.put("tags", p.getTags());
            m.put("viewCount", p.getViewCount());
            m.put("likeCount", p.getLikeCount());
            m.put("createdAt", p.getCreatedAt());
            return m;
        }).toList();

        return Result.ok(Map.of(
                "content", enriched,
                "totalElements", poems.getTotalElements(),
                "totalPages", poems.getTotalPages()
        ));
    }

    /**
     * 我的作品列表
     */
    @GetMapping("/my")
    public Result<List<UserPoem>> myPoems(
            @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) return Result.unauthorized("请先登录");
        return Result.ok(userPoemRepo.findByUserIdAndStatus(userId, 1,
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());
    }

    /**
     * 发布作品
     */
    @PostMapping
    public Result<UserPoem> create(@RequestBody UserPoem poem,
                                    @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) return Result.unauthorized("请先登录");
        if (poem.getTitle() == null || poem.getTitle().isBlank())
            return Result.fail(400, "标题不能为空");
        if (poem.getContent() == null || poem.getContent().isBlank())
            return Result.fail(400, "内容不能为空");
        poem.setUserId(userId);
        poem.setStatus(1); // 直接发布
        poem.setViewCount(0L);
        poem.setLikeCount(0L);
        return Result.ok(userPoemRepo.save(poem));
    }

    /**
     * 删除作品
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                                @RequestAttribute(value = "userId", required = false) Long userId) {
        UserPoem p = userPoemRepo.findById(id).orElse(null);
        if (p == null) return Result.notFound("作品不存在");
        if (!p.getUserId().equals(userId)) return Result.forbidden("无权删除");
        p.setStatus(-1);
        userPoemRepo.save(p);
        return Result.okMsg("已删除");
    }
}
