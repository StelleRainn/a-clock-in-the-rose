import re

with open("/Users/rainn/Projects/a-clock-inside-the-rose/appendix/初稿副本.md", "r", encoding="utf-8") as f:
    text = f.read()

# Define new abstract
new_abstract = """摘要

随着移动互联网的渗透和信息密度的指数级增长，现代知识工作者的注意力资源正经历严重的碎片化剥削。要在充满干扰的数字媒介中维持“深度工作（Deep Work）”状态并妥善统筹个人事务，被普遍视为维系个体竞争力的先决条件。审视现阶段的市场方案，传统的待办清单（Todo List）工具多半局限于线性的排期罗列，几乎不对实际的执行过程进行干预。与此相对，各类独立的番茄钟（Pomodoro Timer）软件在辅助专注方面确有成效，但往往难以同长线的任务规划或数据复盘形成完整的逻辑闭环。基于这种功能割裂、数据孤岛以及智能化干预缺失的现状，本课题设计并实现了一套名为 A Clock Inside the Rose (ACIR) 的个人效率中心系统。该系统尝试将任务管控、沉浸式心流引导、多维数据量化与智能化辅助诊断有机熔于一炉。

在技术架构层面，ACIR 依托前后端分离的现代 Web 体系。后端服务由基于 Java 的 Spring Boot 3 框架承载，借助 MyBatis 完成对底层数据的定制化持久操作，接口交互严格遵循 RESTful API 规范，以此赋予系统良好的纵向扩展能力。前端呈现端选用了 Vue 3 及其推崇的 Composition API 范式，配合 Vite 的极速构建机制与定制化 Element Plus 组件库，搭建起兼具高性能与流畅体验的单页应用（SPA）。数据资产的存储被交由 MySQL 8.0 关系型数据库负责，开发期间针对大跨度的时间聚合查询实施了专门的索引策略与算法调优。

聚焦本研究的工程实践与核心亮点，可以归纳为以下几个维度。首当其冲的是交互层面的“沉浸式心流（Immersive Flow）”重构。区别于常规管理后台生硬的栅格堆砌，系统深度应用了 Glassmorphism 设计语言及 Hero Timer 首屏布局策略。配合动态背景色提取算法，非必要视觉元素的干扰被大幅削弱，进而促使用户更为顺畅地切入专注状态。针对行为数据的反哺，本系统引入“自我量化（Quantified Self）”理念。通过在后端下推复杂的 SQL 聚合运算，前端得以渲染出类似 GitHub 风格的年度热力图（Heatmap）以及直观映射精力分配的玫瑰图（Rose Chart）。更具探索性的一步在于生成式大语言模型（LLM）在垂直场景的融合。借助 Google Gemini API 并创新性地引入“上下文注入（Context Injection）”机制，AI 助手不再局限于泛泛而谈，而是能够实时感知宿主的专注流水与任务积压状况，提供极具针对性的效率诊断。至于跨平台访问，基于响应式降级与移动端 Touch 事件的专项优化，系统在移动设备上展现出了媲美原生应用的流畅度。

经过多轮测试验证，系统各模块运行稳定且响应敏捷，预期的各项设计目标均已达成。本项目的落地不仅从工程角度验证了 Spring Boot 结合 Vue 3 这一全栈方案的高效性，也在一定程度上为人机交互（HCI）法则与大模型在个人生产力赛道的结合，提供了一份具有参考价值的实践样本。

关键词：Spring Boot；Vue 3；个人效率中心；沉浸式体验；上下文注入；数据量化"""

# Replace abstract
# Find from "摘要\n\n随着" to "数据可视化\n\nAbstract"
abstract_pattern = r"摘要\n\n随着.*?关键词：.*?数据可视化"
text = re.sub(abstract_pattern, new_abstract, text, flags=re.DOTALL)

with open("/Users/rainn/Projects/a-clock-inside-the-rose/appendix/初稿副本.md", "w", encoding="utf-8") as f:
    f.write(text)
