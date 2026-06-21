package org.com.it.jassistant.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("game_session")
public class GameSessionPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话ID */
    @TableId(value = "id")
    private String id;
    //创建时间
    @TableField(value = "create_at")
    private LocalDateTime createAt;
    //创建人
    @TableField(value = "create_by")
    private String createBy;
    //创建名称
    @TableField(value = "create_name")
    private String createName;

}