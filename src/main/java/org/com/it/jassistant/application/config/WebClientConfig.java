package org.com.it.jassistant.application.config;

import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * 定制 Spring AI 流式调用底层使用的 WebClient(Reactor Netty)。
 *
 * <p>问题背景:DashScope 等网关会在连接空闲一段时间后从服务端主动断开 keep-alive 连接,
 * 而 Reactor Netty 默认连接池不会主动剔除这些失效连接,下次请求复用就会抛出
 * {@code java.io.EOFException: SSL peer shut down incorrectly}。这里通过缩短空闲连接的
 * 可复用时间并开启后台剔除,从根本上避免复用到“半死”的连接。
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClientCustomizer aiHttpClientCustomizer() {
        ConnectionProvider provider = ConnectionProvider.builder("ai")
                .maxIdleTime(Duration.ofSeconds(5))        // 空闲超过 5s 的连接不再复用,关键参数
                .maxLifeTime(Duration.ofSeconds(55))       // 连接最长存活时间
                .evictInBackground(Duration.ofSeconds(30)) // 后台定期剔除失效连接
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .responseTimeout(Duration.ofMinutes(2))    // 流式 + 思考链路较慢,放宽响应超时
                .keepAlive(true);

        return builder -> builder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
