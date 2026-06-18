-- 会话表增加标题（一会话一标题）
ALTER TABLE `session`
    ADD COLUMN title VARCHAR(255) NULL COMMENT '会话标题';

-- 问答记录关联到会话
ALTER TABLE qa_record
    ADD COLUMN session_id VARCHAR(64) NULL COMMENT '所属会话ID';

CREATE INDEX idx_qa_record_session_id ON qa_record (session_id);
