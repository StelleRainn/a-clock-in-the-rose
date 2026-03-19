import re

with open("/Users/rainn/Projects/a-clock-inside-the-rose/appendix/初稿副本.md", "r", encoding="utf-8") as f:
    text = f.read()

# Here we will parse the parts.
# Abstract is from "摘要\n\n随着" to the start of "Abstract"
# Chapter 1 is from "第一章 绪论" to "第二章 相关技术介绍"
# Chapter 2 is from "第二章 相关技术介绍" to "第三章 系统需求分析" (I should read up to Chapter 3 first).
