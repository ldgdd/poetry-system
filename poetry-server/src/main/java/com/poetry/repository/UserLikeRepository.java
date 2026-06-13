package com.poetry.repository;

import com.poetry.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    // 用户是否已点赞某诗词
    Optional<UserLike> findByUserIdAndPoemId(Long userId, Long poemId);

    // 用户是否已点赞某用户作品
    Optional<UserLike> findByUserIdAndUserPoemId(Long userId, Long userPoemId);

    // 统计某诗词点赞数
    long countByPoemId(Long poemId);

    // 统计某用户作品点赞数
    long countByUserPoemId(Long userPoemId);

    // 取消点赞
    void deleteByUserIdAndPoemId(Long userId, Long poemId);
    void deleteByUserIdAndUserPoemId(Long userId, Long userPoemId);

    boolean existsByUserIdAndPoemId(Long userId, Long poemId);
    boolean existsByUserIdAndUserPoemId(Long userId, Long userPoemId);
}
