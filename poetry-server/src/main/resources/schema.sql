-- ============================================================
-- 诗词雅集 — 完整数据库建表脚本
-- Database: poetry_db
-- ============================================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(100) COMMENT '昵称',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    bio VARCHAR(500) COMMENT '个人简介',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0封禁 -1删除',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
    lock_time DATETIME COMMENT '锁定时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_phone (phone),
    INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 作者表
CREATE TABLE IF NOT EXISTS authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '作者姓名',
    dynasty VARCHAR(20) COMMENT '朝代',
    bio TEXT COMMENT '作者简介',
    avatar VARCHAR(500) COMMENT '头像',
    poem_count INT DEFAULT 0 COMMENT '作品数量',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0隐藏',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_authors_dynasty (dynasty),
    INDEX idx_authors_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作者表';

-- 3. 官方诗词表（扩展现有poems）
CREATE TABLE IF NOT EXISTS poems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '诗名',
    author_id BIGINT COMMENT '作者ID',
    author VARCHAR(50) COMMENT '作者名（冗余字段，方便查询）',
    dynasty VARCHAR(20) COMMENT '朝代',
    category VARCHAR(50) COMMENT '分类: 唐诗/宋词/元曲/诗经/乐府等',
    tags VARCHAR(500) COMMENT '标签，逗号分隔: 山水,边塞,爱情,哲理等',
    content TEXT NOT NULL COMMENT '诗词原文',
    translation TEXT COMMENT '白话译文',
    appreciation TEXT COMMENT '赏析/点评',
    background TEXT COMMENT '创作背景',
    view_count BIGINT DEFAULT 0 COMMENT '浏览量',
    like_count BIGINT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0隐藏',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_poems_author (author_id),
    INDEX idx_poems_dynasty (dynasty),
    INDEX idx_poems_category (category),
    INDEX idx_poems_view (view_count),
    INDEX idx_poems_like (like_count),
    FULLTEXT INDEX ft_poems_content (title, author, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='官方诗词表';

-- 4. 用户创作诗词表
CREATE TABLE IF NOT EXISTS user_poems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '作者用户ID',
    title VARCHAR(200) NOT NULL COMMENT '作品标题',
    content TEXT NOT NULL COMMENT '诗词正文',
    description TEXT COMMENT '创作说明/背景',
    tags VARCHAR(500) COMMENT '标签',
    status TINYINT DEFAULT 0 COMMENT '状态: 0待审核 1已发布 2草稿 -1已删除',
    view_count BIGINT DEFAULT 0 COMMENT '浏览量',
    like_count BIGINT DEFAULT 0 COMMENT '点赞数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_up_user (user_id),
    INDEX idx_up_status (status),
    INDEX idx_up_created (created_at),
    FULLTEXT INDEX ft_up_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户作品表';

-- 5. 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    target_id BIGINT NOT NULL COMMENT '目标ID（诗词ID或用户作品ID）',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型: poem/user_poem',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID（支持二级回复）',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 -1已删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_comment_target (target_id, target_type),
    INDEX idx_comment_user (user_id),
    INDEX idx_comment_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 6. 收藏表（统一收藏诗词 + 用户作品）
CREATE TABLE IF NOT EXISTS user_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    poem_id BIGINT COMMENT '官方诗词ID',
    user_poem_id BIGINT COMMENT '用户作品ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_collection_user (user_id),
    INDEX idx_collection_poem (poem_id),
    INDEX idx_collection_up (user_poem_id),
    UNIQUE KEY uk_user_poem (user_id, poem_id),
    UNIQUE KEY uk_user_up (user_id, user_poem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 7. 点赞表（统一点赞诗词 + 用户作品）
CREATE TABLE IF NOT EXISTS user_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    poem_id BIGINT COMMENT '官方诗词ID',
    user_poem_id BIGINT COMMENT '用户作品ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_like_user (user_id),
    INDEX idx_like_poem (poem_id),
    INDEX idx_like_up (user_poem_id),
    UNIQUE KEY uk_user_poem_like (user_id, poem_id),
    UNIQUE KEY uk_user_up_like (user_id, user_poem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 8. 关注表
CREATE TABLE IF NOT EXISTS user_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    following_id BIGINT NOT NULL COMMENT '被关注者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follow (follower_id, following_id),
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- 9. 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收通知的用户ID',
    sender_id BIGINT COMMENT '触发通知的用户ID',
    type VARCHAR(30) NOT NULL COMMENT '类型: like/comment/follow/collection',
    target_id BIGINT COMMENT '目标ID',
    target_type VARCHAR(20) COMMENT '目标类型: poem/user_poem',
    content VARCHAR(500) COMMENT '通知摘要',
    is_read TINYINT DEFAULT 0 COMMENT '已读: 0未读 1已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notif_user (user_id, is_read),
    INDEX idx_notif_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
