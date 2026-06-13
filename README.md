# 🏯 诗词雅集

一个融合古典文化与现代 AI 技术的中文诗词 Web 平台。支持经典诗词赏析、AI 辅助创作、藏头诗生成、诗友论坛交流等功能。

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Java 23 + Spring Boot 3.4.5 |
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus（深度定制古风主题） |
| 图标方案 | Iconify + MDI（Material Design Icons） |
| 状态管理 | Pinia |
| 数据库 | MySQL 8.0 |
| ORM | Spring Data JPA + Hibernate |
| 认证 | JWT（jjwt 0.12.6）+ BCrypt |
| AI 接口 | DeepSeek API |

## ✨ 功能模块

### 📚 诗词大全
- 20 首经典古诗词（含译文、赏析、创作背景）
- 多维度筛选：朝代 / 分类 / 关键词搜索
- 竖排文字展示（`writing-mode: vertical-rl`）
- 点赞、收藏、评论

### 🤖 AI 创作
- **藏头诗生成** — 输入 2-8 字，AI 生成对应藏头诗
- **主题创作** — 输入主题/关键词 + 选择风格，AI 创作古典诗词
- 生成结果一键复制 / 发布到论坛

### ✍️ 诗友论坛
- 用户发布原创诗词（支持 AI 辅助 + 手动创作）
- 作品点赞、评论互动
- 所有已发布作品公开展示

### 👤 用户系统
- 注册 / 登录（JWT Token 认证）
- BCrypt 密码加密 + 登录失败锁定机制
- 个人主页：收藏列表、创作列表、获赞统计
- 路由守卫（未登录拦截）

### 🛡️ 管理后台
- 管理员账号：`admin` / `123456`
- 诗词录入（完整字段：标题/作者/内容/译文/赏析/背景）
- 评论管理（删除违规评论）
- 用户作品管理（删除不当作品）

## 📁 项目结构

```
poetry_system/
├── poetry-server/                  # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/poetry/
│       ├── PoetryApplication.java       # 启动类
│       ├── common/Result.java           # 统一返回格式
│       ├── config/
│       │   ├── CorsConfig.java          # 跨域配置
│       │   ├── WebConfig.java           # 拦截器 + BCrypt Bean
│       │   └── DataInitializer.java     # 管理员账号初始化
│       ├── controller/
│       │   ├── AuthController.java      # 登录/注册
│       │   ├── AppreciationController.java  # 诗词CRUD
│       │   ├── AcrosticController.java  # 藏头诗
│       │   ├── CreationController.java  # AI创作
│       │   ├── CommentController.java   # 评论
│       │   ├── LikeController.java      # 点赞
│       │   ├── UserPoemController.java  # 用户作品
│       │   └── AdminController.java     # 管理后台
│       ├── dto/                         # 请求/响应 DTO
│       ├── exception/                   # 全局异常处理
│       ├── interceptor/                 # JWT 拦截器
│       ├── model/                       # 9 个实体类
│       ├── repository/                  # 8 个 JPA Repository
│       ├── service/                     # 业务逻辑层
│       └── util/JwtUtil.java            # JWT 工具
│
├── poetry-client/                  # Vue 3 前端
│   ├── package.json
│   ├── vite.config.mjs
│   └── src/
│       ├── api/index.js                 # API 封装 + Token 拦截
│       ├── assets/main.css              # 全局样式（Element Plus 主题定制）
│       ├── components/
│       │   ├── NavBar.vue               # 导航栏
│       │   ├── PoemCarousel.vue         # 首页轮播
│       │   ├── PoemCard.vue             # 诗词卡片
│       │   └── DynastyZone.vue          # 朝代专区
│       ├── router/index.js              # 路由 + 守卫
│       ├── stores/auth.js               # Pinia 认证状态
│       └── views/
│           ├── HomeView.vue             # 首页
│           ├── AppreciationView.vue     # 诗词大全
│           ├── PoemDetailView.vue       # 诗词详情（竖排）
│           ├── AcrosticView.vue         # 藏头诗生成
│           ├── CreationView.vue         # 主题创作
│           ├── CommunityView.vue        # 诗友论坛
│           ├── LoginView.vue            # 登录
│           ├── RegisterView.vue         # 注册
│           ├── ProfileView.vue          # 个人主页
│           └── AdminView.vue            # 管理后台
│
└── README.md
```

## 🗄️ 数据库表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 用户 | username, password(BCrypt), email, role(USER/ADMIN), status |
| `poems` | 官方诗词 | title, author, dynasty, content, translation, appreciation, tags |
| `authors` | 作者 | name, dynasty, bio |
| `user_poems` | 用户创作 | user_id, title, content, status(0待审/1发布/2草稿/-1删除) |
| `comments` | 评论 | user_id, target_id, target_type, content, parent_id |
| `user_collections` | 收藏 | user_id, poem_id, user_poem_id |
| `user_likes` | 点赞 | user_id, poem_id, user_poem_id |
| `user_follows` | 关注 | follower_id, following_id |
| `notifications` | 通知 | user_id, type, target_id, is_read |

## 🚀 快速启动

### 环境要求

- JDK 23+
- MySQL 8.0+
- Node.js 24+
- Maven 3.9+ 或 IntelliJ IDEA（内置 Maven）

### 1. 数据库配置

编辑 `poetry-server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码      # ← 修改这里

deepseek:
  api:
    key: 你的DeepSeek API Key    # ← 修改这里
```

### 2. 启动后端

```bash
cd poetry-server

# 方式 A：命令行（需配置 JAVA_HOME）
set JAVA_HOME=D:\
mvn spring-boot:run

# 方式 B：IntelliJ IDEA
# 用 IDEA 打开 poetry-server 目录 → 右键 PoetryApplication → Run
```

后端启动后访问：`http://localhost:8088`

### 3. 启动前端

```bash
cd poetry-client
npm install    # 首次运行需安装依赖
npm run dev
```

前端启动后访问：`http://localhost:5173`

### 4. 管理员账号

系统首次启动会自动创建管理员账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `123456` | 管理员 |

## 📡 API 一览

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | - |
| POST | `/api/auth/login` | 用户登录 | - |
| GET | `/api/auth/me` | 当前用户信息 | Token |
| GET | `/api/poems` | 诗词列表/搜索 | - |
| GET | `/api/poems/{id}` | 诗词详情 | - |
| GET | `/api/poems/daily` | 每日一诗 | - |
| GET | `/api/poems/hot` | 热门排行 | - |
| POST | `/api/acrostic/generate` | 藏头诗生成 | - |
| POST | `/api/creation/generate` | AI 诗词创作 | - |
| GET | `/api/comments` | 评论列表 | - |
| POST | `/api/comments` | 发表评论 | Token |
| DELETE | `/api/comments/{id}` | 删除评论 | Token |
| POST | `/api/likes/toggle` | 点赞/取消 | Token |
| GET | `/api/favorites` | 收藏列表 | Token |
| POST | `/api/favorites` | 添加收藏 | Token |
| DELETE | `/api/favorites/{poemId}` | 取消收藏 | Token |
| GET | `/api/user-poems` | 论坛作品列表 | - |
| POST | `/api/user-poems` | 发布作品 | Token |
| GET | `/api/user-poems/my` | 我的作品 | Token |
| POST | `/api/admin/poems` | 录入诗词 | Admin |
| DELETE | `/api/admin/comments/{id}` | 删除评论 | Admin |
| DELETE | `/api/admin/user-poems/{id}` | 删除作品 | Admin |

## 🎨 界面特色

- **古风配色** — 米白底 + 墨黑字 + 朱红点缀 + 金色强调
- **矢量图标** — Iconify MDI 7000+ 统一线条风格
- **水墨动效** — 路由过渡动画、水墨扩散 hover、淡入上浮列表
- **竖排诗文** — 详情页 CSS `writing-mode: vertical-rl`
- **印章元素** — Logo、朝代卡片、功能入口

## 📝 诗词数据

系统预置 20 首经典古诗词，涵盖：

- **唐诗**：李白《静夜思》《将进酒》、杜甫《春望》《望岳》、王维《山居秋暝》等
- **宋词**：苏轼《水调歌头》《念奴娇》《定风波》、李清照《声声慢》、岳飞《满江红》等
- **元曲**：马致远《天净沙·秋思》
- **古诗**：陶渊明《饮酒》

管理员可通过后台录入更多诗词（含译文、赏析、背景）。

## 🔒 安全机制

- 密码 BCrypt 加密存储
- JWT Token 24 小时有效期
- 登录连续失败 5 次锁定 30 分钟
- 管理员接口权限校验
- 软删除（status 标记，不物理删除）
- 跨站请求 CORS 配置

## 📄 许可证

MIT License

---

> 🌙 愿诗词之美，跨越千年，与你相遇。
