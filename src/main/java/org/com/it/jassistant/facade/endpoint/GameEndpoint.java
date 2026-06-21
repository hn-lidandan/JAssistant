package org.com.it.jassistant.facade.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.com.it.jassistant.facade.vo.SessionVo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-game")
public class GameEndpoint {

    @GetMapping("/sessions/id")
    @Operation(description = "生成会话ID")
    public SessionVo generateSessionId() {

        return null;
    }


    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "聊天")
    public Flux<String> chat(
            @RequestParam("prompt") String prompt,
            @RequestParam("session_id") String sessionId) {

        return null;
    }

}
