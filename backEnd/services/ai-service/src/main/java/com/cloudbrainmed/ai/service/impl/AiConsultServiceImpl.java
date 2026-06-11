package com.cloudbrainmed.ai.service.impl;

import com.cloudbrainmed.ai.service.AiConsultService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiConsultServiceImpl implements AiConsultService {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Override
    public Map<String, Object> analyze(Map<String, String> request) {
        ChatClient chatClient = chatClientBuilder.build();

        String chiefComplaint = request.getOrDefault("chiefComplaint", "");
        String recordDesc = request.getOrDefault("recordDesc", "");
        String patientAge = request.getOrDefault("patientAge", "未知");
        String patientGender = request.getOrDefault("patientGender", "未知");

        String systemPrompt = """
            你是一位经验丰富的临床辅助诊断专家，正在协助执业医生进行接诊分析。

            请严格按以下 JSON 格式返回分析结果（不要包含任何其他文字）：

            {
              "diagnosis": [
                {"name": "疑似诊断名称", "probability": "高/中/低", "basis": "诊断依据一句话"}
              ],
              "exams": [
                {"name": "建议检查项目", "purpose": "检查目的", "urgency": "紧急/常规"}
              ],
              "advice": "临床处理建议，2-3句话",
              "risk": "需要紧急关注的风险点，如无则填'暂无特殊风险'"
            }

            要求：
            - 基于循证医学
            - 诊断按概率从高到低排列
            - 最多给出 3 个疑似诊断
            - 最多推荐 3 个检查项目
            - 如信息不足以判断，请在 advice 中说明需要补充哪些信息
            """;

        String userPrompt = String.format("""
            患者信息：
            - 年龄：%s
            - 性别：%s

            主诉：%s

            病历描述：%s

            请给出分析结果。
            """, patientAge, patientGender, chiefComplaint, recordDesc);

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(userPrompt));

        Prompt prompt = new Prompt(messages);
        String reply = chatClient.prompt(prompt).call().content();

        // 解析 AI 返回的 JSON
        return parseAiReply(reply);
    }

    /**
     * 解析 AI 返回的 JSON 字符串，兜底处理格式异常
     */
    private Map<String, Object> parseAiReply(String reply) {
        try {
            // 尝试提取 JSON 部分（去掉 markdown 代码块包装）
            String json = reply;
            if (reply.contains("```json")) {
                json = reply.substring(reply.indexOf("```json") + 7);
                if (json.contains("```")) {
                    json = json.substring(0, json.indexOf("```"));
                }
            } else if (reply.contains("```")) {
                json = reply.substring(reply.indexOf("```") + 3);
                if (json.contains("```")) {
                    json = json.substring(0, json.indexOf("```"));
                }
            }
            json = json.trim();

            // 使用 Jackson 或简单的 Map 解析
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            // 解析失败时返回原始文本作为兜底
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("diagnosis", Collections.emptyList());
            fallback.put("exams", Collections.emptyList());
            fallback.put("advice", "AI 分析结果解析异常，原始回复：" + (reply != null ? reply.substring(0, Math.min(200, reply.length())) : "空"));
            fallback.put("risk", "无法解析风险评估");
            return fallback;
        }
    }
}
