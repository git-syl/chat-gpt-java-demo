package com.demo.chatgptreactor.repository;

import com.demo.chatgptreactor.po.ChatPrompt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ReactiveChatPromptRepository extends ReactiveCrudRepository<ChatPrompt, Long> {
    Mono<ChatPrompt> getChatPromptByType(String type);

    Flux<ChatPrompt> findByEnabled(boolean enabled);

    Flux<ChatPrompt> findByEnabledAndModelType(boolean enabled,String modelType);
}