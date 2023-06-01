package com.demo.chatgptreactor.config;

import com.demo.chatgptreactor.api.ChatGptService;
import com.demo.chatgptreactor.hanlder.ServiceException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by syl nerosyl@live.com on 2023/5/24
 *
 * @author syl
 */
@Configuration
public class WebClientConfig {


    @Bean
    public WebClient webClient(ChatGptConfig chatGptConfig) {

        ChatGptConfig.Api api = chatGptConfig.getApi();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, api.getConnectTimeoutMillis())
                .responseTimeout(Duration.ofMillis(api.getResponseTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(api.getReadTimeout(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(api.getWriteTimeout(), TimeUnit.MILLISECONDS)));


        ChatGptConfig.Proxy proxy = chatGptConfig.getProxy();
        if (proxy.getEnabled()) {
             httpClient =   httpClient.proxy(proxyOptions -> proxyOptions.type(ProxyProvider.Proxy.HTTP)
                    .host(proxy.getHost())
                    .port(proxy.getPort())
            );
        }

   return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(api.getBaseUrl())
                .defaultStatusHandler(HttpStatusCode::isError,
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(string -> Mono.just(new ServiceException(resp.statusCode().value(), string, ""))))
                .build();
    }



    @Bean
    public ChatGptService chatGptService(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient)).build();
        return factory.createClient(ChatGptService.class);

    }


}
