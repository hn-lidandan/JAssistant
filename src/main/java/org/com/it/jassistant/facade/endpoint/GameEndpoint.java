package org.com.it.jassistant.facade.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.com.it.jassistant.application.service.GameService;
import org.com.it.jassistant.facade.vo.GirlDetailVo;
import org.com.it.jassistant.facade.vo.QuestionVo;
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

    private final GameService gameService;

    @GetMapping(value = "/chat")
    @Operation(description = "聊天")
    public GirlDetailVo chat(
            @RequestParam("prompt") String prompt,
            @RequestParam("session_id") String sessionId) {

        return gameService.chat(prompt, sessionId);
    }


    @GetMapping("/question")
    @Operation(description = "产生随机生气问题")
    public QuestionVo  generateQuestion() {

        return gameService.generateQuestion();
    }

}
