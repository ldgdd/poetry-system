package com.poetry.controller;

import com.poetry.common.Result;
import com.poetry.model.Comment;
import com.poetry.model.User;
import com.poetry.repository.CommentRepository;
import com.poetry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired private CommentRepository commentRepo;
    @Autowired private UserRepository userRepository;

    /**
     * 获取评论列表（含用户名）
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "poem") String targetType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Comment> comments = commentRepo
                .findByTargetIdAndTargetTypeAndParentIdIsNullAndStatus(
                        targetId, targetType, 1,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        // 批量查用户名
        List<Long> userIds = comments.stream().map(Comment::getUserId).distinct().toList();
        Map<Long, String> usernameMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));

        // 组装带用户名的数据
        List<Map<String, Object>> enriched = comments.getContent().stream().map(c -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("userId", c.getUserId());
            m.put("username", usernameMap.getOrDefault(c.getUserId(), "用户" + c.getUserId()));
            m.put("targetId", c.getTargetId());
            m.put("targetType", c.getTargetType());
            m.put("content", c.getContent());
            m.put("parentId", c.getParentId());
            m.put("likeCount", c.getLikeCount());
            m.put("createdAt", c.getCreatedAt());
            return m;
        }).toList();

        return Result.ok(Map.of(
                "content", enriched,
                "totalElements", comments.getTotalElements(),
                "totalPages", comments.getTotalPages()
        ));
    }

    /**
     * 发表评论
     */
    @PostMapping
    public Result<Comment> create(@RequestBody Comment comment,
                                   @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) return Result.unauthorized("请先登录");
        comment.setUserId(userId);
        comment.setStatus(1);
        return Result.ok(commentRepo.save(comment));
    }

    /**
     * 删除评论（软删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                                @RequestAttribute(value = "userId", required = false) Long userId) {
        Comment c = commentRepo.findById(id).orElse(null);
        if (c == null) return Result.notFound("评论不存在");
        if (!c.getUserId().equals(userId)) return Result.forbidden("无权删除");
        c.setStatus(-1);
        commentRepo.save(c);
        return Result.okMsg("已删除");
    }
}
