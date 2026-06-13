package com.poetry.repository;

import com.poetry.model.Poem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PoemRepository extends JpaRepository<Poem, Long> {

    // 多条件搜索：标题/作者/内容模糊匹配 + 朝代/分类/标签筛选
    @Query("SELECT p FROM Poem p WHERE p.status = 1 AND " +
           "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.author LIKE %:keyword% OR p.content LIKE %:keyword%) AND " +
           "(:dynasty IS NULL OR p.dynasty = :dynasty) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:tag IS NULL OR p.tags LIKE %:tag%)")
    Page<Poem> search(@Param("keyword") String keyword,
                      @Param("dynasty") String dynasty,
                      @Param("category") String category,
                      @Param("tag") String tag,
                      Pageable pageable);

    // 按朝代查询
    Page<Poem> findByDynastyAndStatus(String dynasty, Integer status, Pageable pageable);

    // 按作者查询
    Page<Poem> findByAuthorIdAndStatus(Long authorId, Integer status, Pageable pageable);

    // 按浏览量排行
    Page<Poem> findByStatusOrderByViewCountDesc(Integer status, Pageable pageable);

    // 按点赞数排行
    Page<Poem> findByStatusOrderByLikeCountDesc(Integer status, Pageable pageable);

    // 随机获取一首（每日一诗）
    @Query(value = "SELECT * FROM poems WHERE status = 1 ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Poem> findRandomOne();

    // 按分类查询
    Page<Poem> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
}
