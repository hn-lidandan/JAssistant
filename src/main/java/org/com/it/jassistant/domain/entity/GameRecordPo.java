package org.com.it.jassistant.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@TableName("game_record")
public class GameRecordPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    //所属会话ID
    @TableField(value = "session_id")
    private String sessionId;
    //女生（AI）说的内容
    @TableField(value = "girl_content")
    private String girlContent;
    //男生（玩家）说的内容
    @TableField(value = "boy_content")
    private String boyContent;
    //当前分数
    @TableField(value = "current_score")
    private int currentScore;
    //变动量（正=加，负=减）
    @TableField(value = "change_score")
    private int changeScore;
    //创建时间
    @TableField(value = "create_at")
    private LocalDateTime createAt;
    // 用户ID
    @TableField(value = "create_by")
    private String createBy;
    // 用户名称
    @TableField(value = "create_name")
    private String createName;

}
