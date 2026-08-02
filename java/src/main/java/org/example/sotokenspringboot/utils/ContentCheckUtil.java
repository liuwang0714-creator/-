package org.example.sotokenspringboot.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContentCheckUtil {

    // ====================== 配置 ======================

    /**
     * DeepSeek API Key
     */
    private static final String API_KEY =
                "sk-************";

    /**
     * DeepSeek 官方接口
     */
    private static final String API_URL =
            "https://api.deepseek.com/chat/completions";

    /**
     * HTTP客户端
     */
    private static final OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

    // ====================== 本地违规词库 ======================

    private static final List<String> ILLEGAL_KEYWORDS = Arrays.asList(

            // 辱骂
            "傻逼",
            "脑残",
            "废物",
            "滚蛋",
            "去死",
            "有病",

            // 广告引流
            "加我微信",
            "加微信",
            "扫码联系",
            "扫码领取",
            "加Q",
            "QQ群",
            "TG群",
            "飞机群",


            // 毒品
            "吸毒",
            "贩毒",
            "冰毒",
            "海洛因",

            // 暴力
            "砍人",
            "杀人",
            "灭口",
            "爆头",

            // 恶意行为
            "带节奏",
            "水军",
            "恶意差评"
    );

    /**
     * 本地规则审核
     */
    public static String ruleCheck(String text) {

        if (text == null || text.trim().isEmpty()) {
            return "无违规";
        }

        String content = text.trim().toLowerCase();

        for (String keyword : ILLEGAL_KEYWORDS) {

            if (content.contains(keyword.toLowerCase())) {

                System.out.println("【规则审核命中】关键词：" + keyword);

                return "有违规";
            }
        }

        return "无违规";
    }

    /**
     * AI审核
     */
    public static String aiCheckContent(String inputMsg) {

        try {

            Headers headers = new Headers.Builder()
                    .add("Authorization", "Bearer " + API_KEY)
                    .add("Content-Type", "application/json")
                    .build();

            JSONObject body = new JSONObject();

            // DeepSeek模型
//            body.put("model", "deepseek-chat");
            body.put("model", "deepseek-reasoner");

            // 审核场景固定输出
            body.put("temperature", 0);

            body.put("max_tokens", 10);

            // ====================== System Prompt ======================

            JSONObject systemMsg = new JSONObject();

            systemMsg.put("role", "system");

            systemMsg.put("content",
                    """
                    你是一个严格的内容审核系统。

                    请判断用户输入内容是否违规。

                    违规内容包括：

                    1. 色情低俗
                    2. 辱骂攻击
                    3. 引流广告
                    4. 赌博毒品
                    5. 暴力威胁
                    6. 恶意引战
                    7. 涉及违法犯罪
                    8. 恶意骚扰

                    返回规则：

                    违规：
                    有违规

                    不违规：
                    无违规

                    只能返回：
                    有违规
                    或
                    无违规

                    不允许输出任何解释。
                    """
            );

            // ====================== User Message ======================

            JSONObject userMsg = new JSONObject();

            userMsg.put("role", "user");

            userMsg.put("content", inputMsg);

            body.put("messages",
                    Arrays.asList(systemMsg, userMsg));

            // ====================== 创建请求 ======================

            Request request = new Request.Builder()
                    .url(API_URL)
                    .headers(headers)
                    .post(RequestBody.create(
                            body.toJSONString(),
                            MediaType.parse("application/json")
                    ))
                    .build();

            // ====================== 执行请求 ======================

            try (Response response =
                         HTTP_CLIENT.newCall(request).execute()) {

                String responseBody = "";

                if (response.body() != null) {
                    responseBody = response.body().string();
                }

                System.out.println("DeepSeek返回：");
                System.out.println(responseBody);

                // 请求失败
                if (!response.isSuccessful()) {

                    System.out.println("【AI审核失败】HTTP状态码："
                            + response.code());

                    // 默认放行
                    return "无违规";
                }

                // ====================== JSON解析 ======================

                JSONObject resJson = JSON.parseObject(responseBody);

                if (resJson == null) {

                    System.out.println("【AI审核失败】JSON解析失败");

                    return "无违规";
                }

                JSONArray choices = resJson.getJSONArray("choices");

                if (choices == null || choices.isEmpty()) {

                    System.out.println("【AI审核失败】choices为空");

                    return "无违规";
                }

                JSONObject choiceObj =
                        choices.getJSONObject(0);

                if (choiceObj == null) {
                    return "无违规";
                }

                JSONObject messageObj =
                        choiceObj.getJSONObject("message");

                if (messageObj == null) {
                    return "无违规";
                }

                String result =
                        messageObj.getString("content");

                if (result == null) {

                    System.out.println("【AI审核失败】content为空");

                    return "无违规";
                }

                // ====================== 清洗结果 ======================

                result = result
                        .replace(" ", "")
                        .replace("\n", "")
                        .replace("\r", "")
                        .replace("。", "")
                        .trim();

                System.out.println("审核结果：" + result);

                // ====================== 判断结果 ======================

                if (result.contains("有违规")) {
                    return "有违规";
                }

                if (result.contains("无违规")) {
                    return "无违规";
                }

                // 未知结果默认放行
                return "无违规";
            }

        } catch (Exception e) {

            System.out.println("【AI审核异常】");

            e.printStackTrace();

            // 异常默认放行
            return "无违规";
        }
    }

    /**
     * 最终审核
     */
    public static String finalCheck(String text) {

        // 空内容直接放行
        if (text == null || text.trim().isEmpty()) {
            return "无违规";
        }

        // 第一层：关键词审核
        String ruleResult = ruleCheck(text);

        if ("有违规".equals(ruleResult)) {

            System.out.println("【规则审核拦截】");

            return "有违规";
        }

        // 第二层：AI审核
        String aiResult = aiCheckContent(text);

        System.out.println("【AI审核结果】" + aiResult);

        return aiResult;
    }

    /**
     * 测试
     */
    public static void main(String[] args) {

        String text = "加我微信领取福利";

        String result = finalCheck(text);

        System.out.println("最终结果：" + result);
    }
}