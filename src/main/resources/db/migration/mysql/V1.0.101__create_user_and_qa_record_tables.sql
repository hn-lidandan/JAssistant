CREATE TABLE IF NOT EXISTS user (
    id VARCHAR(64) NOT NULL COMMENT '主键',
    user_name VARCHAR(225) NOT NULL COMMENT '用户名称',
    status VARCHAR(64) NOT NULL COMMENT '状态',
    create_at TIMESTAMP(6) NOT NULL COMMENT '创建时间',
    phone VARCHAR(64) NULL COMMENT '电话',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户表';


CREATE TABLE IF NOT EXISTS qa_record (
    id VARCHAR(64) NOT NULL COMMENT '主键',
    question JSON NOT NULL COMMENT '提问',
    answer JSON NULL COMMENT '回答',
    create_at TIMESTAMP(6) NOT NULL COMMENT '创建时间',
    create_by VARCHAR(64) NOT NULL COMMENT '创建人',
    create_name VARCHAR(255) COMMENT '创建人名称',
    PRIMARY KEY (id),
    INDEX idx_qa_record_create_by (create_by),
    INDEX idx_qa_record_create_at (create_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '问答记录表';
