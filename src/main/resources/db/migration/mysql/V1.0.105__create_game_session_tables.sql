CREATE TABLE IF NOT EXISTS game_session (
    id VARCHAR(64) NOT NULL COMMENT '会话ID',
    create_at TIMESTAMP(6) NOT NULL COMMENT '创建时间',
    create_by VARCHAR(64) NOT NULL COMMENT '创建人',
    create_name VARCHAR(255) COMMENT '创建人名称',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '游戏会话表';




CREATE TABLE IF NOT EXISTS game_record (
    id VARCHAR(64) NOT NULL COMMENT '主键',
    session_id VARCHAR(64) NULL COMMENT '所属游戏会话ID',
    girl_content TEXT NOT NULL COMMENT '女生（AI）说的内容',
    boy_content TEXT NULL COMMENT '男生（玩家）说的内容',
    current_score int NOT NULL  COMMENT '当前分数',
    change_score int NULL  COMMENT '变动量（正=加，负=减）',
    create_at TIMESTAMP(6) NOT NULL COMMENT '创建时间',
    create_by VARCHAR(64) NOT NULL COMMENT '用户ID',
    create_name VARCHAR(255) COMMENT '用户名称',

    PRIMARY KEY (id),
    INDEX idx_game_record_create_by (create_by),

    INDEX idx_game_record_create_at (create_at)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = '游戏聊天记录表';


