package org.com.it.jassistant.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionInfoVo {

    //会话ID
    private String sessionId;
    //标题
    private String title;
    //创建时间
    private LocalDateTime createAt;
}
