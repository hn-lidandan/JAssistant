package org.com.it.jassistant.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@TableName("qa_record")
public class QaRecordPo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    //所属会话ID
    @TableField(value = "session_id")
    private String sessionId;
    //提问
    private String question;
    //回答
    private String answer;
    //创建时间
    @TableField(value = "create_at")
    private LocalDateTime createAt;
    // 创建人
    @TableField(value = "create_by")
    private String createBy;
    // 创建名称
    private String createName;

}
