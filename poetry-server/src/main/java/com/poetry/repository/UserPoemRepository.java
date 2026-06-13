package com.poetry.repository;

import com.poetry.model.UserPoem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPoemRepository extends JpaRepository<UserPoem, Long> {

    // 用户作品列表
    Page<UserPoem> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    // 用户草稿
    Page<UserPoem> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, Integer status, Pageable pageable);

    // 已发布作品（按时间倒序）
    Page<UserPoem> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    // 按标签模糊搜索
    Page<UserPoem> findByStatusAndTagsContaining(Integer status, String tag, Pageable pageable);

    // 全文搜索（标题或内容匹配）
    @Query("SELECT up FROM UserPoem up WHERE up.status = :status AND " +
           "(up.title LIKE %:keyword% OR up.content LIKE %:keyword%)")
    Page<UserPoem> search(@Param("keyword") String keyword,
                          @Param("status") Integer status,
                          Pageable pageable);

    // 全部已发布（分页）
    Page<UserPoem> findByStatus(Integer status, Pageable pageable);
}
