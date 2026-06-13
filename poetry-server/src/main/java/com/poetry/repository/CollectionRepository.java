package com.poetry.repository;

import com.poetry.model.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    // 用户的诗词收藏
    Page<Collection> findByUserIdAndPoemIdIsNotNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 用户的用户作品收藏
    Page<Collection> findByUserIdAndUserPoemIdIsNotNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 是否已收藏某诗词
    boolean existsByUserIdAndPoemId(Long userId, Long poemId);

    // 是否已收藏某用户作品
    boolean existsByUserIdAndUserPoemId(Long userId, Long userPoemId);

    // 取消收藏
    void deleteByUserIdAndPoemId(Long userId, Long poemId);
    void deleteByUserIdAndUserPoemId(Long userId, Long userPoemId);
}
