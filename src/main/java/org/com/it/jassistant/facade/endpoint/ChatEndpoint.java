package org.com.it.jassistant.facade.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.com.it.jassistant.application.service.ChatService;
import org.com.it.jassistant.facade.vo.SessionDetailVo;
import org.com.it.jassistant.facade.vo.SessionInfoVo;
import org.com.it.jassistant.facade.vo.SessionVo;
import org.com.it.jassistant.facade.vo.common.ResultPageVo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatEndpoint {

    @Resource
    private ChatService chatService;

    @GetMapping("/sessions/id")
    @Operation(description = "生成会话ID")
    public SessionVo generateSessionId() {

        return chatService.generateSessionId();
    }


    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "聊天")
    public Flux<String> chat(
            @RequestParam("prompt") String prompt,
            @RequestParam("session_id") String sessionId) {

        return chatService.chat(prompt, sessionId);
    }


    @GetMapping("/sessions")
    @Operation(description = "历史会话列表")
    public ResultPageVo<SessionInfoVo> sessionInfoVoList(
            @RequestParam(value = "limit", defaultValue = "20") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {


        return chatService.sessionInfoVoList(limit, offset);
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(description = "根据会话Id 获取会话详情")
    public SessionDetailVo sessionDetailVo(@PathVariable("sessionId") String sessionId) {

        return chatService.sessionDetailVo(sessionId);
    }


}
