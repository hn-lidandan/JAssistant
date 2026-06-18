CREATE TABLE IF NOT EXISTS session (
    id VARCHAR(64) NOT NULL COMMENT '会话ID',
    create_at TIMESTAMP(6) NOT NULL COMMENT '创建时间',
    create_by VARCHAR(64) NOT NULL COMMENT '创建人',
    create_name VARCHAR(255) COMMENT '创建人名称',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '会话表';


