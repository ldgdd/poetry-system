package com.poetry.repository;

import com.poetry.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    // 关注
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 取消关注
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 关注数
    long countByFollowerId(Long followerId);

    // 粉丝数
    long countByFollowingId(Long followingId);
}
