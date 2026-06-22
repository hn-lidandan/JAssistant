package org.com.it.jassistant.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QuestionVo {

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "女友随机生气问题")
    private String question;

    @Schema(description = "当前分数")
    private int initScore = 20;


}
