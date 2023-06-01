package com.demo.chatgptreactor.config;

import com.demo.chatgptreactor.utils.JtokkitUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reactor.netty.transport.ProxyProvider;


/**
 * Created by syl nerosyl@live.com on 2023/5/24
 *
 * @author syl
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chat-gpt")
public class ChatGptConfig {

    private final OpenAi openAi = new OpenAi();
    private final Api api = new Api();
    private final Proxy proxy = new Proxy();


    @Data
    public static class Proxy {

        @NotNull
        private Boolean enabled;
        private ProxyProvider.Proxy type;
        private String host;
        private int port;
    }


    @Data
    public static class OpenAi {
        /**
         * 系统提示
         */
        @NotBlank
        private String systemPrompt;


        private int initSystemPromptTokens;

        @NotNull
        private int maxTokens;

        @NotBlank
        private String apiKey;


        private void setInitSystemPromptTokens(int initSystemPromptTokens) {
            this.initSystemPromptTokens = initSystemPromptTokens;
        }


    }


    @Data
    public static class Api {
        @NotBlank
        private String baseUrl;
        private int connectTimeoutMillis;
        private int responseTimeout;
        private int readTimeout;
        private int writeTimeout;


    }


    @PostConstruct
    public void init() {
        this.openAi.initSystemPromptTokens = JtokkitUtils.countTokens(this.openAi.systemPrompt);
        if (this.openAi.maxTokens < this.openAi.initSystemPromptTokens) {
            throw new IllegalArgumentException("maxTokens is too small ");
        }
    }


}

