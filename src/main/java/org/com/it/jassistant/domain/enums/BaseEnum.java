package org.com.it.jassistant.domain.enums;

import java.util.Collections;
import java.util.List;

public interface BaseEnum {
    /**
     * 返回字典名
     *
     * @return
     */
    String getDictName();

    /**
     * 返回字典中文名
     *
     * @return
     */
    String getDictCName();

    /**
     * 返回代码值
     *
     * @return
     */
    String getValue();

    /**
     * 返回描述
     *
     * @return
     */
    String getLabel();

    /**
     * 获取子项列表（支持层级枚举）
     *
     * @return
     */
    default List<? extends BaseEnum> getChildren() {
        return Collections.emptyList();
    }


    /**
     * 获取忽略字典项code
     *
     * @return
     */
    default List<String> getIgnoreItemCodes() {
        return Collections.emptyList();
    }

    /**
     * 获取描述信息
     *
     * @return
     */
    default String getDesc() {
        return "";
    }
}