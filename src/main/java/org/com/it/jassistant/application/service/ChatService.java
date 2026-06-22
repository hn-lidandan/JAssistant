package org.com.it.jassistant.application.service;

import org.com.it.jassistant.facade.vo.SessionDetailVo;
import org.com.it.jassistant.facade.vo.SessionInfoVo;
import org.com.it.jassistant.facade.vo.SessionVo;
import org.com.it.jassistant.facade.vo.common.ResultPageVo;
import reactor.core.publisher.Flux;

public interface ChatService {

    SessionVo generateSessionId();

    Flux<String> chat(String prompt,String sessionId);

    ResultPageVo<SessionInfoVo> sessionInfoVoList(Integer limit, Integer offset);

    SessionDetailVo sessionDetailVo(String sessionId);
}