package org.com.it.jassistant.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("session")
public class SessionPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    //标题
    private String title;
    //创建时间
    private LocalDateTime createAt;
    //创建人
    private String createBy;
    //创建名称
    private String createName;

}