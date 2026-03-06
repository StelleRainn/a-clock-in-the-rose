# A Clock Inside the Rose (ACIR) - 开发计划

**项目目标**: 基于 Spring Boot + Vue 的个人效率中心系统
**核心技术**: Vue 3 (Frontend) + Spring Boot (Backend) + MySQL (Database)
**开发策略**: 前端优先 -> 数据库设计 -> 后端实现 -> 联调

## Phase 1: 前端核心与界面搭建 (Current Focus)
> 目标：完成所有页面的静态展示与交互逻辑，确保用户体验流畅。

- [x] **基础设施搭建**
    - [x] 初始化 Vue 3 + Vite 项目
    - [x] 安装 Element Plus, Axios, Sass, Icons
    - [x] 配置 Vite 代理 (`/api`)
- [x] **路由与布局**
    - [x] 设计 MainLayout (侧边栏 + 顶部栏)
    - [x] 配置 Vue Router
- [ ] **核心模块开发 (交互与逻辑)**
    - [ ] **Pomodoro (番茄钟)**: 实现倒计时逻辑、状态管理 (运行/暂停/重置)、完成通知。
        - *Priority*: High (核心功能)
    - [x] **Tasks (任务管理)**: 实现任务列表展示、添加、删除、状态切换 (使用 Mock 数据 -> 已对接真实 API)。
        - *Priority*: High
    - [ ] **Dashboard (仪表盘)**: 聚合展示今日数据。
    - [ ] **Stats (统计)**: 集成 ECharts/Chart.js 展示图表。
    - [x] **Auth (认证)**: 登录/注册表单验证。

## Phase 1.5: 细节打磨与体验优化 (Completed)
> 目标：在核心功能可用的基础上，提升用户体验和系统稳定性。

- [x] **Task (任务管理) 进阶**:
    - [x] **看板视图 (Kanban View)**: 实现类似 Trello 的拖拽任务管理。
    - [x] **任务详情页**: 支持 Markdown 渲染描述，增加标签 (Tags) 管理。
    - [ ] **筛选与排序**: 支持按优先级、状态、截止时间排序。
- [x] **Pomodoro (番茄钟) 进阶**:
    - [x] **任务绑定**: 启动番茄钟时，允许选择关联当前正在进行的任务。
    - [x] **白噪音 (White Noise)**: 提供背景音效（雨声、咖啡馆等）。
    - [x] **全屏专注模式**: 提供沉浸式 UI。
- [x] **Dashboard (仪表盘) 增强**:
    - [x] **日历视图**: 在日历上展示每日的任务完成情况和专注时长。
    - [ ] **待办事项提醒**: 临近截止日期的任务高亮显示。

## Phase 2.0: 游戏化与个性化 (Completed)
> 目标：引入游戏化机制激励用户，并提供更多个性化选项，增强用户粘性（广度迭代）。

- [x] **Gamification (游戏化系统)**:
    - [x] **等级系统 (Leveling)**: 根据专注时长计算 XP，提升用户等级 (e.g., Novice -> Master)。
    - [x] **成就徽章 (Achievements)**: 达成特定条件解锁徽章 (e.g., "专注 10 小时", "连续打卡 7 天")。
    - [x] **连胜纪录 (Streaks)**: 追踪连续登录/专注天数。
- [x] **Personalization (个性化)**:
    - [x] **用户资料**: 上传头像、修改昵称、个性签名。
    - [ ] **全局主题**: 支持浅色/深色模式切换 (Light/Dark Mode)。
- [x] **Social (社交分享)**:
    - [x] **专注卡片 (Focus Card)**: 生成精美的“今日总结”图片，包含专注时长、完成任务数，方便分享至社交媒体。

## Phase 3: 深度优化与移动端适配 (Next Milestone)
> 目标：在功能广度覆盖后，深耕细节体验与多端支持。

- [ ] **Dark Mode (深色模式)**: 
    - [ ] 设计全局 CSS 变量系统。
    - [ ] 适配 Element Plus 深色主题。
    - [ ] 实现跟随系统/手动切换。
- [ ] **Data Export (数据导出)**: 
    - [ ] 支持导出专注记录 (CSV/Excel)。
    - [ ] 支持导出任务清单 (JSON)。
- [ ] **Mobile Adaptation (移动端适配)**: 
    - [ ] 优化侧边栏在小屏幕下的折叠逻辑。
    - [ ] 优化 Dashboard 和 Task List 的卡片布局。
- [ ] **Advanced Task Features (任务进阶)**:
    - [ ] **子任务 (Subtasks)**: 任务嵌套。
    - [ ] **重复任务 (Recurring Tasks)**: 每日/每周重复。
    - [ ] **标签系统 (Tags)**: 自定义标签与筛选。

## Phase 4: 系统联调与部署 (Ongoing)
> 目标：前后端对接，完善系统。

- [x] **接口联调**: 前端 Axios 对接真实后端 API。
- [x] **功能完善**: 错误处理 (404/500)、加载状态、权限控制 (未登录跳转)。
- [ ] **部署准备**: Docker 镜像或常规部署文档。
