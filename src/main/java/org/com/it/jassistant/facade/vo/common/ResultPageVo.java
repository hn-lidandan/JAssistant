package org.com.it.jassistant.facade.vo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页")
public class ResultPageVo<T> {

    @Schema(description = "当前个数")
    private long currentCount;

    @Schema(description = "当前查询数据总数")
    private long total;

    @Schema(description = "结果集")
    private List<T> items;

}
