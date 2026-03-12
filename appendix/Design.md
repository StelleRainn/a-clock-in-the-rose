# ACIR Design 4.0: "Immersive Flow" (沉浸·心流)

> **设计代号**: Phase 4 - Aesthetic & Experience
> **核心理念**: 从“管理工具”转型为“专注空间”。用户打开应用是为了*开始*，而不仅仅是*规划*。

---

## 1. 设计愿景 (Vision)

我们决定推翻原有的“管理后台式”布局（侧边栏 + 仪表盘），拥抱一种更现代、更感性、更具沉浸感的设计语言。

*   **沉浸 (Immersive)**: 界面应当服务于“专注”这一核心行为，而非堆砌数据。
*   **极简 (Minimalist)**: 移除视觉噪点，使用留白和呼吸感来引导用户视线。
*   **灵动 (Fluid)**: 就像 Apple 官网一样，页面应当是流动的，配合丝滑的过渡和微交互。

---

## 2. 核心布局重构 (Layout Redesign)

### 2.1 导航栏变革 (Navigation)
*   **Current**: 固定侧边栏 (Aside Sidebar) —— 典型的 SaaS/Admin 后台风格，效率高但略显僵硬。
*   **New**: **顶部悬浮导航 (Header Navigation)**。
    *   **样式**: 类似于 macOS 顶栏或 Apple 官网。背景采用 **Glassmorphism (毛玻璃)** 效果，半透明模糊，让背景色隐约透出。
    *   **行为**: 始终吸顶 (Sticky) 或 上滑出现/下滑隐藏。
    *   **内容**:
        *   Left: Logo (ACIR 极简图标).
        *   Center: 核心模块 (Home / Tasks / Stats).
        *   Right: 全局功能 (Add Task, Profile, Settings, Theme Toggle).

### 2.2 背景与氛围 (Atmosphere)
*   引入 **动态背景 (Dynamic Backgrounds)** 系统。
    *   **设置**: 在 General 选项中可配置【调整背景图片】。
    *   **范围**: 背景图片仅对 **首页 Pomodoro 的 100vh Hero 区域** 有效。
    *   **过渡**: 首页 Hero 区域的背景图在滚动到次级板块 (Dashboard Widgets) 时，应做 **渐变遮罩** 或 **淡出模糊** 处理，平滑过渡到应用主题色（深/浅）。
    *   **其他页面**: Tasks, Stats 等其他路由页面保持纯净的深/浅模式背景，不显示壁纸。

---

## 3. 页面设计方案 (Page Specifics)

### 3.1 首页：番茄钟优先 (Home: Pomodoro First)
这是本次改版最大的变化。首页不再是 Dashboard，而是 **Focus Station**。

*   **板块一：Hero Section (100vh)**
    *   **布局**: 占据首屏 100% 高度。
    *   **核心元素**: 
        *   **快捷模式切换**: 计时器上方增加三个快捷胶囊按钮 (Pomodoro, Short Break, Long Break)，点击即切换模式并重置时长。
        *   中央巨大的 **计时器 (Timer)**。字体采用超细或超粗的现代无衬线体 (Inter / SF Pro Display)。
        *   极简控制按钮 (Start / Pause / Stop) —— 设计为圆形或胶囊形，悬浮感。
        *   **Zen Note**: 底部居中一个半透明的输入框，随时捕捉想法。
        *   **当前任务**: 计时器上方显示当前绑定的任务（"Working on: Refactor Code"）。
    *   **交互**: 鼠标移动时 UI 显现，静止时 UI 淡出，极致沉浸。

*   **板块二：Dashboard Widgets (Below the fold)**
    *   **交互**: 用户向下滚动 (Scroll Down) 时，Hero Section 背景模糊/变暗，下方内容浮现。
    *   **样式**: 采用 **Bento Grid (便当盒布局)**。
    *   **内容**:
        *   Heatmap (热力图) 卡片。
        *   Today's Focus (今日时长) 环形图。
        *   Upcoming Tasks (近期任务) 列表。
    *   **特效**: 滚动时的 **Fade-in / Slide-up** 动画。

### 3.2 任务中心 (Tasks)
由于取消了侧边栏，任务界面将获得更宽阔的横向空间。

*   **容器化**: 内容包裹在居中的半透明容器中 (Container)，而非铺满全屏，保持阅读舒适度。
*   **List View**: 
    *   行高增加，更像 iOS 的 Reminders。
    *   Hover 效果增强（光标跟随或高亮）。
*   **Kanban View**:
    *   横向滚动体验优化。
    *   卡片设计更轻量，减少边框，利用阴影和背景色区分层级。

### 3.3 统计分析 (Stats)
*   **可视化升级**: 图表背景透明化，直接融合进整体的大背景中。
*   **Rose Chart & Heatmap**: 调整配色以适应新的全局背景（深色/高对比度）。

---

## 4. 全局设置 (Settings)
重构设置弹窗，采用 **Sidebar + Content** 布局。

*   **General**: 
    *   **背景图片**: 选择本地预设壁纸 (Day/Night) 或纯色。
*   **Timers**: 
    *   自定义 Pomodoro, Short Break, Long Break 的时长 (分钟)。
    *   自动开始休息/番茄钟的开关。
*   **Sounds**: 
    *   选择提示音 (Bell, Alarm, etc.)。
    *   音量调节滑块。
*   **Account**: 用户信息管理。

---

## 5. 实施路线图 (Implementation Roadmap)

1.  **Step 1: 基础架构调整** (Completed)
    *   修改 `MainLayout.vue`: 移除 `el-aside`，新建 `HeaderNav.vue`。
    *   配置全屏背景容器。

2.  **Step 2: 首页重构 (Home View)** (Completed)
    *   拆分 Home 为 `TimerSection.vue` (Hero) 和 `DashboardWidgets.vue` (Scroll content)。
    *   实现 100vh 布局和滚动侦测。

3.  **Step 3: 组件样式升级** (Completed)
    *   重写卡片 (`el-card`) 样式，实现 Glassmorphism。
    *   引入 Inter 字体。

4.  **Step 4: 细节迭代 (Current Focus)**
    *   **快捷按钮**: 首页增加 Pomodoro/Break 切换按钮。
    *   **设置升级**: 实现多 Tab 设置弹窗 (背景、时长、声音)。
    *   **背景逻辑**: 实现首页 Hero 背景淡出逻辑。

---

*此文档作为 Phase 4 的指导纲领，后续开发中可根据实际效果灵活迭代。*
