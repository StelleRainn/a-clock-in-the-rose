# Mobile Adaptation Plan (移动端适配方案)

> **目标**: 在 Phase 5 中实现全平台的无缝体验，将 ACIR 从桌面端扩展到移动端，打造“随时随地”的专注空间。

## 1. 核心策略 (Core Strategy)

*   **Mobile First Navigation**: 摒弃桌面端的顶部双导航栏，采用更符合拇指操作习惯的底部标签栏 (Bottom TabBar)。
*   **Responsive Layout**: 
    *   **Dashboard**: Bento Grid 从多列变为单列流式布局。
    *   **Tasks/Stats**: 优化列表和图表的在小屏幕上的展示密度。
*   **Touch Friendly**: 增大点击热区，优化拖拽手势 (Touch Events)。
*   **Native-like Experience**: 隐藏滚动条，增加过渡动画，模拟原生应用质感。

---

## 2. 导航重构 (Navigation Redesign)

### 2.1 底部导航栏 (Bottom TabBar)
*   **组件**: 新增 `MobileNavBar.vue`。
*   **显示逻辑**: 仅在视口宽度 `< 768px` (MD断点) 时显示，同时隐藏 `HeaderNav`。
*   **布局**: 固定底部 (Fixed Bottom)，高度 `60px` + `safe-area-inset-bottom`。
*   **内容**:
    1.  **Focus**: (Home/Timer) - 首页
    2.  **Calendar**: (Calendar) - 日历
    3.  **Add**: (FAB - Floating Action Button) - 居中突出的“添加任务”按钮
    4.  **Tasks**: (List) - 任务列表
    5.  **Profile**: (User) - 个人中心 & 设置 (替代原顶部右侧的 Profile Dropdown)

### 2.2 顶部区域 (Top Area)
*   在移动端隐藏全局 Header 后，各页面需自行处理顶部状态栏区域：
    *   **Dashboard**: 保持全屏背景，无顶部栏，Logo 可缩小悬浮于左上角或隐藏。
    *   **Tasks/Stats/Calendar**: 增加一个简易的 Mobile Header (标题 + 设置/筛选按钮)。

---

## 3. 页面适配细节 (Page Adaptation)

### 3.1 首页 (Focus Station)
*   **Hero Timer**: 
    *   字体大小调整：从 `12rem` 缩小至 `6rem` 以适应竖屏。
    *   布局调整：垂直居中，确保在软键盘弹出时不被遮挡。
*   **Bento Grid**:
    *   强制 **单列布局 (1 Column)**。
    *   Widget 高度自适应，不再强制固定宽高比。
*   **Zen Note**:
    *   移动端点击输入框时，自动滚动到可视区域，避免被键盘遮挡。

### 3.2 任务列表 (Tasks)
*   **List View**:
    *   隐藏复杂的表格列 (如 Created At)，仅保留 Title, Priority, Due Date。
    *   行高增加至 `64px`，方便手指点击。
    *   左滑菜单 (Swipe Actions): 实现左滑完成/删除任务 (类似 iOS Reminders)。
*   **Kanban View**:
    *   **禁用** 移动端看板视图？或者改为 **横向分页滑动** (Swiper)，每次只显示一列状态。
    *   *建议*: 移动端默认强制使用 List View，Kanban 体验在手机上通常不佳。

### 3.3 统计 (Stats)
*   **Charts**:
    *   ECharts 容器高度调整：从固定高度改为 `aspect-ratio: 1/1` 或 `4/3`。
    *   Tooltip 优化：改为点击触发 (Click) 而非悬停 (Hover)。
    *   Heatmap: 减少显示的月份数量（如只显示最近 3-6 个月），或改为垂直滚动的热力图。

### 3.4 智能助手 (Intelligent)
*   **Chat UI**:
    *   输入框固定在底部键盘上方。
    *   历史记录侧边栏改为 **抽屉式 (Drawer)**，点击左上角图标滑出。

---

## 4. 技术实现 (Implementation)

### 4.1 CSS 架构
*   使用 CSS Media Queries (`@media (max-width: 768px)`) 控制布局切换。
*   引入 `postcss-px-to-viewport` (可选) 或手动使用 `rem`/`vw` 单位进行字号适配。
*   CSS 变量适配 Safe Area:
    ```css
    padding-bottom: env(safe-area-inset-bottom);
    ```

### 4.2 组件拆分
*   `MainLayout.vue`: 
    ```html
    <template>
      <div class="main-layout">
        <HeaderNav class="hidden-mobile" /> <!-- Desktop -->
        <main>...</main>
        <MobileNavBar class="visible-mobile" /> <!-- Mobile -->
      </div>
    </template>
    ```

### 4.3 交互优化
*   **Touch Events**: 引入 `@use-gesture/vue` 或使用 Vue 原生 Touch 事件处理滑动手势。
*   **Vibration**: 在番茄钟完成、任务勾选时调用 `navigator.vibrate()` 提供触觉反馈。

---

## 5. 任务清单 (Action Items)

- [ ] **Step 1: 基础框架**
    - [ ] 创建 `MobileNavBar.vue` 组件。
    - [ ] 修改 `MainLayout.vue` 响应式逻辑。
    - [ ] 调整全局 CSS (Typography, Spacing) 适配小屏。
- [ ] **Step 2: 首页适配**
    - [ ] `TimerSection.vue` 响应式样式。
    - [ ] `DashboardWidgets.vue` 单列布局。
- [ ] **Step 3: 功能页适配**
    - [ ] `TaskListView.vue` 移动端样式优化。
    - [ ] `StatsOverview.vue` 图表重绘适配。
    - [ ] `AiAssistantView.vue` 侧边栏改为抽屉。
- [ ] **Step 4: 细节打磨**
    - [ ] 增加 PWA Manifest (图标、启动屏) 以支持“添加到主屏幕”。
    - [ ] 测试 iOS Safari 底部地址栏遮挡问题。

