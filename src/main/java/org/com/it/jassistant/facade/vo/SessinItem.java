package org.com.it.jassistant.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessinItem {

    //提问
    private String question;
    //回答
    private String answer;
    //创建时间
    private LocalDateTime createAt;
}
