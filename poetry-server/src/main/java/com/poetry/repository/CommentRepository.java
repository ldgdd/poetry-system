package com.poetry.repository;

import com.poetry.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 查询某目标的顶级评论（parent_id为null）
    Page<Comment> findByTargetIdAndTargetTypeAndParentIdIsNullAndStatus(
            Long targetId, String targetType, Integer status, Pageable pageable);

    // 查询某评论的子回复
    List<Comment> findByParentIdAndStatus(Long parentId, Integer status);

    // 用户的所有评论
    Page<Comment> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    // 统计某目标的评论数
    long countByTargetIdAndTargetTypeAndStatus(Long targetId, String targetType, Integer status);
}
