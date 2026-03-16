# 毕业论文撰写指南 (Thesis Writing Guidance)

> **项目名称**: A Clock Inside the Rose (ACIR)
> **课题名称**: 基于 Spring Boot + Vue 的个人效率中心系统设计与实现
> **适用阶段**: 毕业设计论文撰写 / 答辩准备

---

## 1. 论文基调与核心亮点 (Key Highlights)

在撰写论文时，应避免将其写成简单的“代码说明书”。ACIR 的演进过程（从基础功能到沉浸式体验，再到 AI 赋能）本身就是一篇很好的工程实践叙事。

**核心亮点（应在摘要、实现章节和总结中反复强调）：**

1.  **沉浸式体验 (Immersive Flow)**: 突破传统管理后台的刻板印象，采用 **Glassmorphism (毛玻璃)** 设计语言与 **Hero Timer** 首屏布局，探索了 Web 应用在“心流体验”上的可能性。（对应 Milestone 4）
2.  **数据驱动 (Data-Driven)**: 不仅是记录，更通过 **Heatmap (热力图)**、**Rose Chart (玫瑰图)** 等高阶图表实现自我量化 (Quantified Self)。（对应 Milestone 3）
3.  **智能化辅助 (AI Integration)**: 引入 LLM (Google Gemini)，通过 **Context Injection (上下文注入)** 技术，让 AI 读取用户的专注数据与待办事项，提供个性化的效率诊断，而非简单的通用对话。（对应 Milestone 5）
4.  **多端适配 (Cross-Platform Adaptation)**: 实现了响应式布局与移动端专属交互（如底部导航栏、触摸优化），打破桌面端限制，支持“随时随地”的专注体验。（对应 Milestone 5）
5.  **工程化完备性**: 完整的前后端分离架构，包含 JWT 认证、RESTful API 设计、MyBatis 复杂查询优化、前端状态管理 (Pinia) 等标准工程实践。

---

## 2. 论文大纲与内容映射 (Outline & Mapping)

建议按照标准的软件工程硕士/本科毕业论文结构进行组织。以下是各章节建议内容及对应的项目模块。

### 第一章 绪论 (Introduction)
*   **1.1 研究背景**: 现代人注意力碎片化，传统清单工具缺乏“执行力”辅助。
*   **1.2 国内外现状**: 对比 Todoist (强管理弱统计)、Forest (强专注弱管理)，引出 ACIR “管理+专注+分析+智能”的一体化定位。
*   **1.3 研究意义**: 探索 Web 技术在个人效能工具中的深度应用，以及 AIGC 在垂直场景落地的可行性。

### 第二章 相关技术介绍 (Technology Stack)
*   **前端**: Vue 3 (Composition API), Vite, Element Plus (定制主题), ECharts, Pinia.
*   **后端**: Spring Boot 3.x, MyBatis, MySQL 8.0.
*   **AI**: Google Gemini API (Stream 流式响应).
*   **其他**: Glassmorphism CSS 实现, Apache Commons CSV (导出).

### 第三章 系统需求分析 (Requirements Analysis)
*   **3.1 业务流程分析**: 用户注册 -> 创建任务 -> 开启番茄钟 -> 产出数据 -> AI 分析 -> 优化习惯。
*   **3.2 功能性需求**:
    *   **任务管理**: CRUD, 标签系统, 看板拖拽, 子任务。
    *   **专注计时**: 倒计时逻辑, 白噪音, 沉浸模式 (Zen Note)。
    *   **数据分析**: 热力图, 标签分布, 导出功能。
    *   **AI 助手**: 智能问答, 上下文感知建议。
*   **3.3 非功能性需求**: 响应式布局 (PC/Mobile 自适应), 深色模式适配, 数据安全性 (API Key 加密存储)。

### 第四章 系统设计 (System Design)
*   **4.1 总体架构设计**: 绘制 B/S 架构图 (Client -> Nginx/Vite -> Spring Boot -> MySQL).
*   **4.2 数据库设计 (ER图)**:
    *   核心实体: `User`, `Task`, `Subtask`, `Tag`, `PomodoroRecord`.
    *   AI 扩展: `ChatSession`, `ChatMessage` (如有持久化).
    *   *重点展示 Task 与 Tag 的多对多关系设计，以及 Task 与 Pomodoro 的关联。*
*   **4.3 界面与交互设计 (UI/UX)**:
    *   **设计理念**: 引用 "Immersive Flow" 概念 (详见 `Design.md`)。
    *   **布局重构**: 阐述从“侧边栏”到“顶部双 Header”再到移动端“底部 TabBar”的演进理由。
    *   **响应式策略**: 针对不同屏幕尺寸的布局适配方案 (Bento Grid 单列化、抽屉式侧边栏)。

### 第五章 系统实现 (Implementation)
*这是论文的重头戏，建议选取 3-4 个具有代表性的难点进行深入描写。*

*   **5.1 沉浸式番茄钟的实现**:
    *   前端计时器状态管理 (Pinia)。
    *   动态背景与文字颜色的自适应算法 (`fast-average-color` 的应用)。
*   **5.2 交互式看板 (Kanban) 开发**:
    *   基于 `Sortablejs` 的拖拽排序实现。
    *   解决“列内滚动”与“防误触拖拽”的交互细节。
*   **5.3 可视化统计模块**:
    *   后端复杂 SQL 聚合查询 (MyBatis XML 编写 `SUM`, `GROUP BY`)。
    *   前端 ECharts 组件封装与深色模式适配逻辑。
*   **5.4 具备上下文感知的 AI 助手**:
    *   Prompt Engineering: 如何将 `user_stats` 和 `todo_list` 组装进 System Prompt。
    *   流式对话 (Streaming) 的前端渲染实现。
*   **5.5 移动端适配与响应式开发**:
    *   **导航栏重构**: 利用 CSS Media Queries 实现 PC 端顶部导航与移动端底部 TabBar 的智能切换。
    *   **交互优化**: 针对触摸屏优化的点击热区调整与抽屉式 (Drawer) 侧边栏实现。

### 第六章 系统测试 (Testing)
*   **功能测试**: 编写测试用例 (e.g., “拖拽已完成任务应被拦截”).
*   **兼容性测试**: 深/浅色主题切换测试，不同尺寸屏幕 (Desktop/Mobile) 的布局适配测试。
*   **性能测试**: 首页加载速度，AI 响应延迟。

### 第七章 总结与展望 (Conclusion)
*   **总结**: 完成了预期的 Milestone 1-5 核心功能。
*   **不足**: 白噪音混音功能待完善，移动端 PWA 本地化能力有待进一步探索。
*   **展望**: 引入更多 AI Agent 能力，探索多端同步。

---

## 3. 图表素材准备清单 (Figures Checklist)

高质量的图表是论文的加分项。建议准备以下素材：

1.  **系统用例图 (Use Case Diagram)**: 涵盖普通用户的所有操作。
2.  **数据库 E-R 图**: 清晰展示实体间关系。
3.  **系统架构图**: 前后端分离的技术栈堆叠。
4.  **核心时序图 (Sequence Diagram)**:
    *   番茄钟结束并自动记录数据的流程。
    *   AI 助手发送请求并接收流式响应的流程。
5.  **界面截图 (High-res Screenshots)**:
    *   首页 (Focus Station) 在深/浅模式下的对比。
    *   看板视图 (Kanban) 的拖拽状态。
    *   AI 聊天窗口的上下文分析回复。
    *   统计页面的热力图与玫瑰图。
    *   移动端适配效果图 (首页单列布局、底部导航栏)。

---

## 4. 写作建议 (Tips)

*   **术语规范**: 统一使用专业术语，如 "RESTful API", "SPA (单页应用)", "MVVM", "Prompt Engineering"。
*   **弱化 CRUD**: 不要花费篇幅流水账式地记录“如何实现增删改查”，而是着重描写“拖拽排序的逻辑”、“AI 上下文的构建”、“动态主题的算法”等有技术含量的点。
*   **强调演进**: 可以在“系统设计”章节简要提及设计的迭代过程（如从 Milestone 3 的侧边栏到 Milestone 4 的顶部导航），体现对用户体验的深入思考。

---

*这份指南基于项目现有的文档库整理而成，旨在为你的毕业论文写作提供清晰的脉络。祝写作顺利！*
