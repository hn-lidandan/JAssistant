package org.com.it.jassistant.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class UserPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    // 用户名称
    private String userName;
    // 用户状态
    private String status;
    // 创建时间
    private LocalDateTime createAt;
    // 电话
    private String phone;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", status='" + status + '\'' +
                ", createAt=" + createAt +
                ", phone='" + phone + '\'' +
                '}';
    }
}
