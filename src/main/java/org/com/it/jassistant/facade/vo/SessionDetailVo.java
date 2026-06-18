package org.com.it.jassistant.facade.vo;

import lombok.Data;

import java.util.List;

@Data
public class SessionDetailVo {

    // 标题
    private String title;
    // 对话上下文
    private List<SessinItem> context;


}
