package org.com.it.jassistant.application.service;

import org.com.it.jassistant.facade.vo.GirlDetailVo;
import org.com.it.jassistant.facade.vo.QuestionVo;

public interface GameService {

    GirlDetailVo chat(String prompt, String sessionId);

    QuestionVo generateQuestion();
}
