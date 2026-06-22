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
    //用户ID
    @TableField(value = "create_by")
    private String createBy;
    //用户名称
    @TableField(value = "create_name")
    private String createName;
    //女友初始生气理由
    @TableField(value = "angry_reason")
    private String angryReason;
    //当前分数
    @TableField(value = "init_score")
    private Integer initScore;

}