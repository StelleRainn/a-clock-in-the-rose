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
    - [ ] **Tasks (任务管理)**: 实现任务列表展示、添加、删除、状态切换 (使用 Mock 数据)。
        - *Priority*: High
    - [ ] **Dashboard (仪表盘)**: 聚合展示今日数据。
    - [ ] **Stats (统计)**: 集成 ECharts/Chart.js 展示图表。
    - [ ] **Auth (认证)**: 登录/注册表单验证。

## Phase 2: 数据库设计与建模
> 目标：设计支撑业务的数据库结构。

- [ ] **ER 图设计**: 确定 User, Task, PomodoroRecord, Tag 等实体关系。
- [ ] **Schema 实现**: 编写 SQL 建表脚本。
    - `users`: 用户信息
    - `tasks`: 任务详情 (title, status, due_date, priority)
    - `pomodoro_records`: 专注记录 (start_time, duration, linked_task_id)

## Phase 3: 后端开发 (Spring Boot)
> 目标：提供 RESTful API，替换前端 Mock 数据。

- [ ] **环境配置**: Spring Boot 3.x 初始化, MyBatis/JPA 配置。
- [ ] **API 实现**:
    - Auth: `/api/auth/login`, `/api/auth/register`
    - Tasks: `/api/tasks` (CRUD)
    - Pomodoro: `/api/pomodoro` (Save record)
    - Stats: `/api/stats/daily`
- [ ] **单元测试**: Service 层逻辑验证。

## Phase 4: 系统联调与优化
> 目标：前后端对接，完善系统。

- [ ] **接口联调**: 前端 Axios 对接真实后端 API。
- [ ] **功能完善**: 错误处理、加载状态、权限控制。
- [ ] **部署准备**: Docker 镜像或常规部署文档。
