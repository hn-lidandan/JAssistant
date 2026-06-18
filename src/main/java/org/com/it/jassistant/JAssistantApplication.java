package org.com.it.jassistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@MapperScan("org.com.it.jassistant.domain.entity")
public class JAssistantApplication {

    private static final Logger log = LoggerFactory.getLogger(JAssistantApplication.class);

    public static void main(String[] args) {
        log.info("项目开始启动....");
        SpringApplication.run(JAssistantApplication.class, args);
        log.info("项目开始成功....");
    }

}
