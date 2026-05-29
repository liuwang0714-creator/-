# coding: utf-8
import SparkApi

# 配置信息（从控制台获取）
APP_CONFIG = {
    "appid": "",
    "api_secret": "",
    "api_key": "",
    "domain": "lite",
    "spark_url": "wss://spark-api.xf-yun.com/v1.1/chat"
}


def spark_chat(question: str, context: list = None, max_context_length: int = 8000) -> str:
    """
    调用讯飞星火Lite接口进行对话

    Args:
        question: 用户的问题
        context: 对话上下文列表，格式为 [{"role": "user/assistant/system", "content": "内容"}, ...]
        max_context_length: 上下文最大长度限制，超出会自动删除最早的记录

    Returns:
        星火AI的回答内容
    """
    # 初始化上下文，如果未传入则创建空列表
    chat_context = context.copy() if context else []

    # 添加当前问题到上下文
    chat_context.append({"role": "user", "content": question})

    # 检查并裁剪上下文长度
    def _get_context_length(ctx):
        """计算上下文总长度"""
        return sum(len(item["content"]) for item in ctx)

    # 循环删除最早的记录直到长度符合要求
    while _get_context_length(chat_context) > max_context_length:
        if len(chat_context) > 1:
            del chat_context[0]
        else:
            break  # 至少保留当前问题

    # 调用星火API
    SparkApi.answer = ""
    SparkApi.main(
        APP_CONFIG["appid"],
        APP_CONFIG["api_key"],
        APP_CONFIG["api_secret"],
        APP_CONFIG["spark_url"],
        APP_CONFIG["domain"],
        chat_context
    )

    # 获取回答结果
    answer = SparkApi.answer.strip()


    return answer
