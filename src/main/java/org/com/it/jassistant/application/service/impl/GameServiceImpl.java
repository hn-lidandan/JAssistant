package org.com.it.jassistant.application.service.impl;

import java.util.Collections;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.com.it.jassistant.application.constants.GameQuestionConstants;
import org.com.it.jassistant.application.service.GameService;
import org.com.it.jassistant.domain.entity.*;
import org.com.it.jassistant.domain.enums.RoleType;
import org.com.it.jassistant.facade.vo.GirlDetailVo;
import org.com.it.jassistant.facade.vo.QuestionVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;

@Service
public class GameServiceImpl implements GameService {


    @Resource
    private GameRecordMapper gameRecordMapper;

    @Resource
    private ChatClient gameChatClient;

    @Resource
    private GameSessionMapper gameSessionMapper;

    @Resource
    private ObjectMapper objectMapper;
    /** 作为上下文带入的最近历史轮数 */
    private final static int HISTORY_TURNS = 10;
    private final static int DEFAULT_INIT_SCORE = 20;
    private final static int MAX_INIT_SCORE = 30;
    private final static int MAX_SCORE = 100;
    private final static String SYSTEM_GIRL_ID = "system_girl";
    private final static String SYSTEM_GIRL_NAME = "系统女友";

    private final static String COMMON_USER_ID = "user_001";//暂用这个，后期要加鉴权+用户模块
    private final static String COMMON_USER_NAME = "user_001";//暂用这个，后期要加鉴权+用户模块


    @Override
    @Transactional(rollbackFor = Exception.class)
    public GirlDetailVo chat(String prompt, String sessionId) {
        if (!StringUtils.hasText(prompt) || !StringUtils.hasText(sessionId)) {
            throw new IllegalArgumentException("prompt 和 sessionId 不能为空");
        }

        GameSessionPo sessionPo = gameSessionMapper.selectById(sessionId);
        if (sessionPo == null) {
            throw new IllegalStateException("会话不存在");
        }

        List<GameRecordPo> gameRecordPos = listRecentRecords(sessionId);
        int lastCurrentScore = gameRecordPos.isEmpty()
                ? defaultScore(sessionPo.getInitScore())
                : gameRecordPos.get(gameRecordPos.size() - 1).getCurrentScore();

        // 先落库玩家本轮输入，保证对话流水完整可追溯。
        GameRecordPo reply = new GameRecordPo();
        reply.setSessionId(sessionId);
        reply.setRoleType(RoleType.REPLY.getValue());
        reply.setCreateBy(COMMON_USER_ID);
        reply.setCreateName(COMMON_USER_NAME);
        reply.setContent(prompt);
        reply.setChangeScore(0);
        reply.setCurrentScore(lastCurrentScore);
        insertGameRecord(reply);

        // AI 只负责给出本轮回复和建议分值，最终分数仍以服务端计算结果为准。
        String aiPrompt = buildGamePrompt(sessionPo, gameRecordPos, prompt, lastCurrentScore);
        String aiResponse = gameChatClient.prompt()
                .user(aiPrompt)
                .call()
                .content();
        GameChatAiResult aiResult = parseAiResult(aiResponse);

        // 对模型返回做兜底，避免出现非法分值或越界分数。
        int changeScore = normalizeChangeScore(aiResult.getChangeScore());
        int currentScore = clampScore(lastCurrentScore + changeScore);
        aiResult.setChangeScore(changeScore);
        aiResult.setCurrentScore(currentScore);
        aiResult.setFinished(currentScore <= 0 || currentScore >= MAX_SCORE);

        String girlReply = buildGirlReply(aiResult);

        GameRecordPo girlRecord = new GameRecordPo();
        girlRecord.setSessionId(sessionId);
        girlRecord.setRoleType(RoleType.GIRL.getValue());
        girlRecord.setCreateBy(SYSTEM_GIRL_ID);
        girlRecord.setCreateName(SYSTEM_GIRL_NAME);
        girlRecord.setContent(girlReply);
        girlRecord.setChangeScore(changeScore);
        girlRecord.setCurrentScore(currentScore);
        insertGameRecord(girlRecord);

        GirlDetailVo girlDetailVo = new GirlDetailVo();
        girlDetailVo.setResponse(girlReply);
        girlDetailVo.setChangeScore(changeScore);
        girlDetailVo.setCurentScore(currentScore);
        return girlDetailVo;
    }

    @Override
    public QuestionVo generateQuestion() {
        // 从预置题库中随机选一条开局理由，并按该题目的难度区间生成初始分。
        GameQuestionConstants.QuestionSeed questionSeed = randomQuestionSeed();
        String angryReason = questionSeed.reason();
        int initScore = randomInitScore(questionSeed);
        String sessionID = generateSessionId(angryReason, initScore);

        QuestionVo questionVo = new QuestionVo();
        questionVo.setSessionId(sessionID);
        questionVo.setQuestion(angryReason);
        questionVo.setInitScore(initScore);
        return questionVo;
    }

    /**
     * 创建一局新游戏，并插入一条系统开场记录作为首轮上下文。
     */
    private String generateSessionId(String angryReason,int initScore){
        GameSessionPo sessionPo = new GameSessionPo();
        sessionPo.setCreateAt(LocalDateTime.now());
        sessionPo.setCreateBy(COMMON_USER_ID);
        sessionPo.setCreateName(COMMON_USER_NAME);
        sessionPo.setAngryReason(angryReason);
        sessionPo.setInitScore(initScore);
        //ID 由 userID + 年月日时分 构成 eg:user_001_202606221224
        String id = generateId();
        sessionPo.setId(id);
        gameSessionMapper.insert(sessionPo);

        GameRecordPo gameRecordPo = new GameRecordPo();
        gameRecordPo.setCurrentScore(initScore);
        gameRecordPo.setChangeScore(0);
        gameRecordPo.setRoleType(RoleType.SYSTEM.getValue());
        gameRecordPo.setContent(angryReason);
        gameRecordPo.setSessionId(id);
        gameRecordPo.setCreateBy(SYSTEM_GIRL_ID);
        gameRecordPo.setCreateName(SYSTEM_GIRL_NAME);

        insertGameRecord(gameRecordPo);
        return id;
    }

    /**
     * 生成会话ID：用户ID + 年月日时分
     * 格式：{userId}_{yyyyMMddHHmm}
     */
    private String generateId() {
        // 获取当前时间并格式化为 yyyyMMddHHmm
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        // 组合用户ID和时间戳
        return COMMON_USER_ID + "_" + timestamp;
    }

    private void insertGameRecord(GameRecordPo gameRecordPo) {

        gameRecordPo.setCreateAt(LocalDateTime.now());
        gameRecordMapper.insert(gameRecordPo);
    }

    /**
     * 先按倒序截取最近 N 条，再翻转回正序，便于给模型还原真实对话过程。
     */
    private List<GameRecordPo> listRecentRecords(String sessionId) {
        LambdaQueryWrapper<GameRecordPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameRecordPo::getSessionId, sessionId)
                .orderByDesc(GameRecordPo::getCreateAt)
                .last("limit " + HISTORY_TURNS);
        List<GameRecordPo> gameRecordPos = gameRecordMapper.selectList(wrapper);
        Collections.reverse(gameRecordPos);
        return gameRecordPos;
    }

    /**
     * 运行时 prompt 只放本局上下文，系统级规则由 gameChatClient 的默认 system prompt 承担。
     */
    private String buildGamePrompt(GameSessionPo sessionPo, List<GameRecordPo> history, String prompt, int currentScore) {
        return """
                当前游戏信息：
                - 生气原因：%s
                - 当前原谅值：%d/100

                最近聊天记录（按时间顺序）：
                %s

                用户本轮输入：
                %s

                请基于以上上下文，只返回 JSON。
                """.formatted(
                StringUtils.hasText(sessionPo.getAngryReason()) ? sessionPo.getAngryReason() : "用户惹你生气了，但原因需要你结合上下文自然延续",
                currentScore,
                buildHistoryText(history),
                prompt
        );
    }

    /**
     * 将数据库中的历史记录拼成模型更容易理解的“角色:内容”文本上下文。
     */
    private String buildHistoryText(List<GameRecordPo> history) {
        if (history.isEmpty()) {
            return "暂无历史记录";
        }
        StringBuilder builder = new StringBuilder();
        for (GameRecordPo record : history) {
            builder.append(resolveRoleName(record.getRoleType()))
                    .append(": ")
                    .append(record.getContent())
                    .append(System.lineSeparator());
        }
        return builder.toString().trim();
    }

    /**
     * 将内部角色类型映射成 prompt 中使用的角色名，便于模型区分说话方。
     */
    private String resolveRoleName(String roleType) {
        if (RoleType.REPLY.getValue().equals(roleType)) {
            return "USER";
        }
        if (RoleType.GIRL.getValue().equals(roleType)) {
            return "GIRL";
        }
        return "SYSTEM";
    }

    /**
     * 模型被要求只返回 JSON，这里直接反序列化成结构化结果。
     */
    private GameChatAiResult parseAiResult(String aiResponse) {
        if (!StringUtils.hasText(aiResponse)) {
            throw new IllegalStateException("AI 未返回内容");
        }
        try {
            return objectMapper.readValue(aiResponse, GameChatAiResult.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("AI 返回内容不是合法 JSON: " + aiResponse, ex);
        }
    }

    /**
     * 只接受约定的五档分值，其余情况统一降级为 0。
     */
    private int normalizeChangeScore(Integer changeScore) {
        if (changeScore == null) {
            return 0;
        }
        return switch (changeScore) {
            case -10, -5, 0, 5, 10 -> changeScore;
            default -> 0;
        };
    }

    /**
     * 将分数限制在游戏允许的 0~100 区间内，避免出现越界状态。
     */
    private int clampScore(int score) {
        if (score < 0) {
            return 0;
        }
        return Math.min(score, MAX_SCORE);
    }

    /**
     * 会话还没有任何对话记录时，回退到初始分；初始分缺失则使用默认值。
     */
    private int defaultScore(Integer initScore) {
        return initScore == null ? DEFAULT_INIT_SCORE : initScore;
    }

    /**
     * 从预置开局题库中随机抽取一条生气理由。
     */
    private GameQuestionConstants.QuestionSeed randomQuestionSeed() {
        List<GameQuestionConstants.QuestionSeed> questionSeeds = GameQuestionConstants.QUESTION_SEEDS;
        return questionSeeds.get(ThreadLocalRandom.current().nextInt(questionSeeds.size()));
    }

    /**
     * 按题目配置的分数区间随机生成初始分，同时兜底限制不超过全局上限。
     */
    private int randomInitScore(GameQuestionConstants.QuestionSeed questionSeed) {
        int minScore = Math.max(0, questionSeed.minInitScore());
        int maxScore = Math.min(MAX_INIT_SCORE, questionSeed.maxInitScore());
        if (minScore >= maxScore) {
            return minScore;
        }
        return ThreadLocalRandom.current().nextInt(minScore, maxScore + 1);
    }

    /**
     * 结束提示由服务端统一补齐，避免模型遗漏通关/失败文案。
     */
    private String buildGirlReply(GameChatAiResult aiResult) {
        String reply = StringUtils.hasText(aiResult.getGirlfriendReply())
                ? aiResult.getGirlfriendReply().trim()
                : "请继续游戏。";
        if (!Boolean.TRUE.equals(aiResult.getFinished())) {
            return reply;
        }
        if (aiResult.getCurrentScore() <= 0) {
            return reply + System.lineSeparator() + "游戏结束，你的女朋友已经甩了你！";
        }
        return reply + System.lineSeparator() + "恭喜你通关了，你的女朋友已经原谅你了！";
    }

    @Data
    private static class GameChatAiResult {
        private String emotion;
        private String girlfriendReply;
        private Integer changeScore;
        private Integer currentScore;
        private String scoreReason;
        private Boolean finished;
    }
}
