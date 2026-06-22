package org.com.it.jassistant.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GirlDetailVo {

    @Schema(description = "女友回答")
    private String response;

    @Schema(description = "当前分数")
    private int curentScore;

    @Schema(description = "变动量（正=加，负=减）")
    private int changeScore;
}
