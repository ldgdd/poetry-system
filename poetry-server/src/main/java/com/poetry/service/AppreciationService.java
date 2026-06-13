package com.poetry.service;

import com.poetry.model.Collection;
import com.poetry.model.Poem;
import com.poetry.repository.CollectionRepository;
import com.poetry.repository.PoemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AppreciationService {

    @Autowired
    private PoemRepository poemRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    /**
     * 搜索/列表诗词
     */
    public Page<Poem> searchPoems(String keyword, String dynasty, String category, String tag, int page, int size) {
        return poemRepository.search(keyword, dynasty, category, tag,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    /**
     * 按热度排行
     */
    public Page<Poem> getHotPoems(int page, int size) {
        return poemRepository.findByStatusOrderByViewCountDesc(1, PageRequest.of(page, size));
    }

    /**
     * 每日一诗（随机）
     */
    public Optional<Poem> getDailyPoem() {
        return poemRepository.findRandomOne();
    }

    /**
     * 诗词详情（同时增加浏览量）
     */
    @Transactional
    public Optional<Poem> getPoemById(Long id) {
        Optional<Poem> poem = poemRepository.findById(id);
        poem.ifPresent(p -> {
            long current = p.getViewCount() != null ? p.getViewCount() : 0;
            p.setViewCount(current + 1);
            if (p.getLikeCount() == null) p.setLikeCount(0L);
            poemRepository.save(p);
        });
        return poem;
    }

    /**
     * 添加收藏（官方诗词）
     */
    @Transactional
    public void addPoemFavorite(Long userId, Long poemId) {
        if (!poemRepository.existsById(poemId)) {
            throw new RuntimeException("诗词不存在");
        }
        if (collectionRepository.existsByUserIdAndPoemId(userId, poemId)) {
            throw new RuntimeException("已经收藏过了");
        }
        Collection col = new Collection();
        col.setUserId(userId);
        col.setPoemId(poemId);
        collectionRepository.save(col);
    }

    /**
     * 取消收藏（官方诗词）
     */
    @Transactional
    public void removePoemFavorite(Long userId, Long poemId) {
        collectionRepository.deleteByUserIdAndPoemId(userId, poemId);
    }

    /**
     * 收藏列表（诗词）- 返回完整 Poem 对象
     */
    public Page<Poem> getFavoritePoems(Long userId, int page, int size) {
        Page<Collection> cols = collectionRepository
                .findByUserIdAndPoemIdIsNotNullOrderByCreatedAtDesc(
                        userId, PageRequest.of(page, size));
        List<Long> poemIds = cols.getContent().stream()
                .map(Collection::getPoemId).toList();
        List<Poem> poems = poemRepository.findAllById(poemIds);
        // 保持收藏顺序
        Map<Long, Poem> poemMap = new java.util.LinkedHashMap<>();
        for (Poem p : poems) poemMap.put(p.getId(), p);
        List<Poem> ordered = poemIds.stream()
                .map(poemMap::get).filter(java.util.Objects::nonNull).toList();
        return new org.springframework.data.domain.PageImpl<>(ordered,
                PageRequest.of(page, size), cols.getTotalElements());
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long poemId) {
        return collectionRepository.existsByUserIdAndPoemId(userId, poemId);
    }
}
