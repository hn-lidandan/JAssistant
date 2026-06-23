package org.com.it.jassistant.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.it.jassistant.application.constants.SystemConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // 系统角色
    @Value("${jassistant.ai.system-prompt}")
    private String systemPrompt;

    /**
     * Boot 4 + Jackson 2 组合下，这里显式提供 Bean，避免业务层注入失败。
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * 无状态的 ChatClient：只负责"模型 + 默认角色(system prompt)"。
     * 会话ID、历史记录属于每次请求的运行时状态，由 service 在调用时传入，不在此装配。
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor()) // 添加默认的Advisor,记录日志
                .build();
    }

    @Bean
    public ChatClient gameChatClient(OpenAiChatModel chatModel) {

        return ChatClient.builder(chatModel)
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

}
