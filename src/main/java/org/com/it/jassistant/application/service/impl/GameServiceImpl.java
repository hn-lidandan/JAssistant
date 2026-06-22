package org.com.it.jassistant.application.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.com.it.jassistant.application.service.GameService;
import org.com.it.jassistant.domain.entity.*;
import org.com.it.jassistant.domain.enums.RoleType;
import org.com.it.jassistant.facade.vo.GirlDetailVo;
import org.com.it.jassistant.facade.vo.QuestionVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class GameServiceImpl implements GameService {


    @Resource
    private GameRecordMapper gameRecordMapper;

    @Resource
    private ChatClient gameChatClient;

    @Resource
    private GameSessionMapper gameSessionMapper;
    /** 作为上下文带入的最近历史轮数 */
    private final static int HISTORY_TURNS = 10;

    private final static String COMMON_USER_ID = "user_001";//暂用这个，后期要加鉴权+用户模块
    private final static String COMMON_USER_NAME = "user_001";//暂用这个，后期要加鉴权+用户模块


    @Override
    public GirlDetailVo chat(String prompt, String sessionId) {
        //玩家回答入库
        GameRecordPo reply = new GameRecordPo();
        reply.setRoleType(RoleType.REPLY.getValue());
        reply.setCreateBy(COMMON_USER_ID);
        reply.setCreateName(COMMON_USER_NAME);
        reply.setCreateAt(LocalDateTime.now());
        reply.setContent(prompt);
        reply.setChangeScore(0);
        //
        insertGameRecord(reply);

        // 查询历史聊天记录
        LambdaQueryWrapper<GameRecordPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameRecordPo::getSessionId, sessionId)
                .orderByDesc(GameRecordPo::getCreateAt)
                .last("limit " + HISTORY_TURNS);
        List<GameRecordPo> gameRecordPos = gameRecordMapper.selectList(wrapper);

        // prompt 是玩家的回答
        // 需要 调用gameChatClient 推理出女友回答 和 心情分数(要根)
        //登记表 game_record
       // 组装返回前端

        return null;
    }

    @Override
    public QuestionVo generateQuestion() {

        // 生成 随机生气问题  加 初始化心情分数，满分100分，初始化分数不能超过30分 todo
        String angryReason = "";
        int initScore = 0;
        String sessionID = generateSessionId(angryReason, initScore);

        QuestionVo questionVo = new QuestionVo();
        questionVo.setSessionId(sessionID);
        questionVo.setInitScore(initScore);
        return questionVo;
    }

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
        gameRecordPo.setCreateBy("system_girl");
        gameRecordPo.setCreateName("系统女友");

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
}
