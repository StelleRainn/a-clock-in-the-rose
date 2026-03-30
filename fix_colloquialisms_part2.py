import os

file_path = "/Users/rainn/Projects/a-clock-inside-the-rose/appendix/论文终稿第一版.md"
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

replacements = {
    "硬核背书": "坚实的技术支撑",
    "超脱了简单的": "超越了简单的",
    "止步于任务的罗列": "仅限于任务的简单罗列",
    "死数据": "缺乏活力的静态数据",
    "空泛的套话": "空泛的通用回复",
    "UI 窠臼": "UI 刻板模式",
    "工程突围战": "工程实践环节",
    "长出“业务透视眼”": "具备“业务感知能力”",
    "倒向了现代化的": "采用了现代化的",
    "接过了接力棒": "承担了相应职责",
    "扛起了存储海量流水": "承担了存储海量流水",
    "暗藏了一套": "内置了一套",
    "明文裸奔": "明文传输或存储",
    "防范跨层调用的混乱与职责越界": "防范跨层调用的耦合与职责越界",
    "踩坑成本": "维护成本",
    "拔高了": "提升了"
}

for old, new in replacements.items():
    content = content.replace(old, new)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Part 2 replacements applied successfully.")
