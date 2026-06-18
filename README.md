# JAssistant

JAssistant 是一个基于 Spring Boot 和 Spring AI 开发的 AI 聊天助手项目。当前版本聚焦于基础聊天机器人能力：支持创建会话、流式对话、保存问答记录、查询历史会话和会话详情。后续计划扩展为智能客服系统，并逐步引入 RAG、知识库管理、多用户鉴权等能力。

## 当前功能

- AI 流式聊天：基于 Spring AI ChatClient 调用大模型，接口以 SSE 方式返回回答内容。
- 会话管理：支持生成会话 ID、保存会话标题、查询历史会话列表。
- 上下文记忆：聊天时会读取当前会话最近 10 轮历史问答作为上下文。
- 问答记录落库：每次完整回答结束后，将用户问题和模型回答保存到 MySQL。
- 数据库迁移：使用 Flyway 管理用户表、会话表、问答记录表等数据库结构。
- 连接池优化：针对流式 AI 请求定制 WebClient/Reactor Netty 连接池，减少空闲连接复用导致的异常。

## 后续规划

- 用户体系：增加登录、鉴权、用户隔离和权限控制。
- 智能客服：支持客服场景下的会话分配、工单流转、人工接入等能力。
- RAG 知识库：支持文档上传、切分、向量化、召回和基于知识库的问答。
- 多模型配置：支持 OpenAI 兼容接口、DeepSeek、DashScope 等模型配置切换。
- 管理后台：提供会话、用户、知识库、问答记录等管理能力。

## 技术栈

- Java 17
- Spring Boot 4.1.0
- Spring AI 2.0.0
- MyBatis-Plus 3.5.16
- MySQL
- Flyway
- Reactor Netty
- Lombok

## 项目结构

```text
JAssistant
├── pom.xml
├── mvnw / mvnw.cmd
├── src
│   ├── main
│   │   ├── java/org/com/it/jassistant
│   │   │   ├── JAssistantApplication.java          # 应用入口
│   │   │   ├── application/config                  # AI 与 WebClient 配置
│   │   │   ├── application/service                 # 应用服务
│   │   │   ├── domain/entity                       # 实体与 Mapper
│   │   │   └── facade                              # HTTP 接口与 VO
│   │   └── resources
│   │       ├── application.yaml                    # 应用配置
│   │       └── db/migration/mysql                  # Flyway 数据库迁移脚本
│   └── test
└── README.md
```

## 环境要求

- JDK 17+
- MySQL 8.x
- Maven 3.9+，或直接使用项目自带的 Maven Wrapper

## 快速启动

### 1. 创建数据库

项目默认连接本地 MySQL 数据库：

```sql
CREATE DATABASE ja_ssistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

启动应用时 Flyway 会自动执行 `src/main/resources/db/migration/mysql` 下的数据库迁移脚本。

### 2. 配置数据库和模型

修改 `src/main/resources/application.yaml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ja_ssistant?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
```

AI 模型当前通过 OpenAI 兼容接口接入，可按实际供应商调整 `base-url`、`api-key` 和 `model`：

```yaml
spring:
  ai:
    model:
      chat: openai
    openai:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: ${OPENAI_API_KEY:}
      chat:
        options:
          model: deepseek-v4-pro
          temperature: 0.7
```

建议通过环境变量配置 API Key，避免将密钥提交到代码仓库：

```bash
export OPENAI_API_KEY=your_api_key
```

### 3. 启动项目

```bash
./mvnw spring-boot:run
```

Windows 环境：

```bash
mvnw.cmd spring-boot:run
```

默认服务地址：

```text
http://localhost:8081/jassistant
```

## 接口说明

### 生成会话 ID

```http
GET /jassistant/ai/sessions/id
```

响应示例：

```json
{
  "sessionId": "xxx"
}
```

### 流式聊天

```http
GET /jassistant/ai/chat?prompt=你好&session_id=xxx
```

- `prompt`：用户输入内容。
- `session_id`：会话 ID，需要先调用生成会话 ID 接口获取。
- 响应类型：`text/event-stream`。

示例：

```bash
curl -N "http://localhost:8081/jassistant/ai/chat?prompt=你好&session_id=xxx"
```

### 查询历史会话列表

```http
GET /jassistant/ai/sessions?limit=20&offset=0
```

参数说明：

- `limit`：每页数量，默认 20。
- `offset`：偏移量，默认 0。

### 查询会话详情

```http
GET /jassistant/ai/sessions/{sessionId}
```

返回当前会话标题和历史问答上下文。

## 核心配置

应用配置位于 `src/main/resources/application.yaml`：

```yaml
server:
  port: 8081
  servlet:
    context-path: /jassistant

jassistant:
  ai:
    system-prompt: 你是ai智能助手，你的名字是事事
```

其中：

- `server.port`：应用端口。
- `server.servlet.context-path`：应用访问前缀。
- `jassistant.ai.system-prompt`：默认系统提示词。
- `spring.ai.openai.*`：大模型服务配置。
- `spring.flyway.*`：数据库迁移配置。

## 数据库说明

当前主要数据表：

- `user`：用户表，当前代码中暂未完整启用用户体系。
- `session`：会话表，保存会话 ID、标题、创建人、创建时间。
- `qa_record`：问答记录表，保存用户问题、模型回答、会话 ID、创建信息。

当前服务中使用临时固定用户：

```java
user_001
```

后续接入鉴权和用户模块后，需要将该逻辑替换为真实登录用户。

## 开发命令

运行测试：

```bash
./mvnw test
```

打包：

```bash
./mvnw clean package
```

运行打包产物：

```bash
java -jar target/JAssistant-0.0.1-SNAPSHOT.jar
```

## 注意事项

- 不要在代码仓库中提交真实 API Key、数据库密码等敏感信息。
- 当前聊天接口使用 GET 请求传递 `prompt`，后续如支持长文本、多模态或复杂参数，建议改为 POST。
- 当前上下文窗口按最近 10 条问答记录读取，后续可根据模型上下文长度、token 预算和业务场景动态调整。
- RAG、智能客服和用户体系仍属于规划能力，当前版本尚未完整实现。
