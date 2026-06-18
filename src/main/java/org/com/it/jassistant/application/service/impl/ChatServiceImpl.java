package org.com.it.jassistant.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.com.it.jassistant.application.service.ChatService;
import org.com.it.jassistant.domain.entity.QaRecordMapper;
import org.com.it.jassistant.domain.entity.QaRecordPo;
import org.com.it.jassistant.domain.entity.SessionMapper;
import org.com.it.jassistant.domain.entity.SessionPo;
import org.com.it.jassistant.domain.entity.UserMapper;
import org.com.it.jassistant.facade.vo.SessinItem;
import org.com.it.jassistant.facade.vo.SessionDetailVo;
import org.com.it.jassistant.facade.vo.SessionInfoVo;
import org.com.it.jassistant.facade.vo.SessionVo;
import org.com.it.jassistant.facade.vo.common.ResultPageVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private SessionMapper sessionMapper;

    @Resource
    private QaRecordMapper qaRecordMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ChatClient chatClient;

    private final static String COMMON_USER_ID = "user_001";//暂用这个，后期要加鉴权+用户模块
    private final static String COMMON_USER_NAME = "user_001";//暂用这个，后期要加鉴权+用户模块

    /** 作为上下文带入的最近历史轮数 */
    private final static int HISTORY_TURNS = 10;
    /** 会话标题最大长度 */
    private final static int TITLE_MAX_LEN = 30;


    @Override
    public SessionVo generateSessionId() {

        SessionPo sessionPo = new SessionPo();
        sessionPo.setCreateAt(LocalDateTime.now());
        sessionPo.setCreateBy(COMMON_USER_ID);
        sessionPo.setCreateName(COMMON_USER_NAME);
        int insertCount = sessionMapper.insert(sessionPo);
        if (insertCount != 1 || sessionPo.getId() == null) {
            throw new IllegalStateException("生成会话ID失败");
        }

        SessionVo sessionVo = new SessionVo();
        sessionVo.setSessionId(sessionPo.getId());

        return sessionVo;
    }

    @Override
    public Flux<String> chat(String prompt, String sessionId) {

        // 1. 取最近 N 轮历史（倒序取最新，再正序还原成对话顺序）
        List<QaRecordPo> history = qaRecordMapper.selectList(
                new LambdaQueryWrapper<QaRecordPo>()
                        .eq(QaRecordPo::getSessionId, sessionId)
                        .orderByDesc(QaRecordPo::getCreateAt)
                        .last("limit " + HISTORY_TURNS));
        Collections.reverse(history);

        // 2. 拼装历史消息：user(question) / assistant(answer)
        List<Message> messages = new ArrayList<>();
        for (QaRecordPo qa : history) {
            if (StringUtils.hasText(qa.getQuestion())) {
                messages.add(new UserMessage(qa.getQuestion()));
            }
            if (StringUtils.hasText(qa.getAnswer())) {
                messages.add(new AssistantMessage(qa.getAnswer()));
            }
        }

        // 3. 流式调用（system 角色已在 ChatClient 默认配置中）
        // 4. 累积完整答案，流式结束后落库并回写会话标题
        StringBuilder answer = new StringBuilder();
        return chatClient.prompt()
                .messages(messages)
                .user(prompt)
                .stream()
                .content()
                .doOnNext(answer::append)
                .doOnComplete(() -> saveQaRecord(sessionId, prompt, answer.toString()));
    }

    /**
     * 落库一条问答记录；若该会话尚无标题，用首个提问回写 session.title。
     */
    private void saveQaRecord(String sessionId, String question, String answer) {
        QaRecordPo po = new QaRecordPo();
        po.setSessionId(sessionId);
        po.setQuestion(question);
        po.setAnswer(answer);
        po.setCreateAt(LocalDateTime.now());
        po.setCreateBy(COMMON_USER_ID);
        po.setCreateName(COMMON_USER_NAME);
        qaRecordMapper.insert(po);

        SessionPo session = sessionMapper.selectById(sessionId);
        if (session != null && !StringUtils.hasText(session.getTitle())) {
            String title = question.length() > TITLE_MAX_LEN
                    ? question.substring(0, TITLE_MAX_LEN)
                    : question;
            session.setTitle(title);
            sessionMapper.updateById(session);
        }
    }

    @Override
    public ResultPageVo<SessionInfoVo> sessionInfoVoList(Integer limit, Integer offset) {

        //分页查询表 session
        LambdaQueryWrapper<SessionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SessionPo::getCreateAt);
        long pageSize = limit > 0 ? limit : 10;
        long current = (offset >= 0 && pageSize > 0) ? (offset / pageSize) + 1 : 1;
        Page<SessionPo> page = new Page<>(current, pageSize);

        Page<SessionPo> sessionPage = sessionMapper.selectPage(page, wrapper);

        List<SessionInfoVo> items = sessionPage.getRecords().stream().map(po -> {
            SessionInfoVo vo = new SessionInfoVo();
            vo.setSessionId(po.getId());
            vo.setTitle(po.getTitle());
            vo.setCreateAt(po.getCreateAt());
            return vo;
        }).toList();

        ResultPageVo<SessionInfoVo> resultPageVo = new ResultPageVo<>();
        resultPageVo.setTotal(sessionPage.getTotal());
        resultPageVo.setItems(items);
        resultPageVo.setCurrentCount(items.size());
        return resultPageVo;
    }

    @Override
    public SessionDetailVo sessionDetailVo(String sessionId) {

        SessionDetailVo vo = new SessionDetailVo();
        SessionPo session = sessionMapper.selectById(sessionId);
        if (session == null) {
            vo.setContext(Collections.emptyList());
            return vo;
        }
        vo.setTitle(session.getTitle());

        List<QaRecordPo> records = qaRecordMapper.selectList(
                new LambdaQueryWrapper<QaRecordPo>()
                        .eq(QaRecordPo::getSessionId, sessionId)
                        .orderByAsc(QaRecordPo::getCreateAt));

        List<SessinItem> context = records.stream().map(r -> {
            SessinItem item = new SessinItem();
            item.setQuestion(r.getQuestion());
            item.setAnswer(r.getAnswer());
            item.setCreateAt(r.getCreateAt());
            return item;
        }).toList();
        vo.setContext(context);

        return vo;
    }
}
