-- 添加女友初始生气理由字段
ALTER TABLE game_session
    ADD COLUMN angry_reason TEXT NULL COMMENT '女友初始生气理由' AFTER create_name;

-- 添加初始化分数字段
ALTER TABLE game_session
    ADD COLUMN init_score INT NOT NULL DEFAULT 20 COMMENT '初始化分数' AFTER angry_reason;

-- 1. 删除旧字段
ALTER TABLE game_record
DROP COLUMN girl_content,
DROP COLUMN boy_content;

-- 2. 添加新字段
ALTER TABLE game_record
    ADD COLUMN role_type VARCHAR(64) NOT NULL COMMENT '角色类型(字典role_type)' AFTER session_id,
ADD COLUMN content TEXT NOT NULL COMMENT '说话内容' AFTER role_type;