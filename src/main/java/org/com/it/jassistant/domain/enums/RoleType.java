package org.com.it.jassistant.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType implements BaseEnum{
    SYSTEM("SYSTEM","系统"),
    REPLY("REPLY","玩家");

    private String value;
    private String label;

    @Override
    public String getDictName() {
        return "";
    }

    @Override
    public String getDictCName() {
        return "";
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public String getLabel() {
        return "";
    }
}
