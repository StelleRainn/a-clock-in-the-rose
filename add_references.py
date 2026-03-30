import os

file_path = "/Users/rainn/Projects/a-clock-inside-the-rose/appendix/论文终稿第一版.md"
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

replacements = {
    "所谓心流，通常被定义为一种高度沉浸、忘我且极具创造力的精神阈值。": "所谓心流，通常被定义为一种高度沉浸、忘我且极具创造力的精神阈值[7]。",
    "正如美国计算机学者 Cal Newport 在其理论中所强调的那样，伴随人工智能与自动化浪潮的逼近": "正如美国计算机学者 Cal Newport 在其理论中所强调的那样[6]，伴随人工智能与自动化浪潮的逼近",
    "番茄工作法（Pomodoro Technique）作为一项久经考验的时间管理策略，催生了大量番茄钟应用。": "番茄工作法（Pomodoro Technique）作为一项久经考验的时间管理策略[5]，催生了大量番茄钟应用。",
    "它们通常以 GTD（Getting Things Done）理论为内核，借助复杂的标签过滤系统和自然语言解析技术": "它们通常以 GTD（Getting Things Done）理论为内核[8]，借助复杂的标签过滤系统和自然语言解析技术",
    "难以支撑专业用户对于“自我量化（Quantified Self）”的高阶诉求。": "难以支撑专业用户对于“自我量化（Quantified Self）”[9]的高阶诉求。",
    
    "视线转回国内，诸如滴答清单（TickTick）等头部产品在功能融合方面迈出了实质性的一步，比如在清单体系内原生地嵌入了番茄钟模块。但若将目光投向学术界与开源社区，会发现针对此类复合型效率系统的工程研究依然稍显匮乏。现存的相关课题或毕业设计，由于缺乏自上而下的产品哲学支撑，常常陷入一种“简单耦合的设计误区”——即简单地将一个标准的 CRUD 后台与一个前端倒计时器进行机械的功能堆叠。": 
    "视线转回国内，诸如滴答清单（TickTick）等头部产品在功能融合方面迈出了实质性的一步，比如在清单体系内原生地嵌入了番茄钟模块。在底层技术探索层面，国内学术界与工程界在基于 Spring Boot 与 Vue 架构的系统开发上已积累了丰富的实践经验。例如，崔靖茹等[4]、刘盛等[3]及牛子逸[2]分别探讨了该技术栈在高校信息化项目、微服务科研管理以及在线评阅系统中的工程落地，验证了其在复杂业务场景下的高可用性。同时，随着大模型技术的普及，陈长风[1]尝试将 ChatGLM 引入学习平台以提供个性化推荐，为 AI 赋能传统应用提供了初步范式。\n\n然而，若将目光聚焦于个人效能与时间管理这一垂直赛道，会发现针对此类复合型效率系统的工程研究依然稍显匮乏。现存的相关课题或毕业设计，由于缺乏自上而下的产品哲学支撑，常常陷入一种“简单耦合的设计误区”——即简单地将一个标准的 CRUD 后台与一个前端倒计时器进行机械的功能堆叠。",

    "REST（Representational State Transfer）架构风格的框架内。": "REST（Representational State Transfer）[10]架构风格的框架内。",
    "系统利用 JWT（JSON Web Token）构筑了全新的鉴权护城河。": "系统利用 JWT（JSON Web Token）[11]构筑了全新的鉴权护城河。",
    "数据库 E-R 模型": "数据库 E-R 模型[12]",
    "敲定了严密的响应式降级策略。": "敲定了严密的响应式降级策略[13]。",
    "系统测试并非开发周期的尾声，而是验证软件工程质量、暴露潜在逻辑断层的最终“角斗场”。": "系统测试并非开发周期的尾声，而是验证软件工程质量、暴露潜在逻辑断层的最终“角斗场”[14]。",
    "也为未来可能的微服务化拆分埋下了伏笔。": "也为未来可能的微服务（Microservices）[15]化拆分埋下了伏笔。"
}

for old, new in replacements.items():
    if old in content:
        content = content.replace(old, new)
        print(f"Replaced: {old[:20]}...")
    else:
        print(f"NOT FOUND: {old[:20]}...")

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Reference replacements applied successfully.")
