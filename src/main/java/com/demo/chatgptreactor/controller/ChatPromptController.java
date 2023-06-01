/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.chatgptreactor.controller;

import com.demo.chatgptreactor.po.ChatPrompt;
import com.demo.chatgptreactor.repository.ReactiveChatPromptRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * Created by syl nerosyl@live.com on 2023/5/15
 *
 * @author syl
 */
@Slf4j
@RestController
@RequestMapping("/chat-prompt")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ChatPromptController {

    @NonNull
    ReactiveChatPromptRepository reactiveChatPromptRepository;

    @GetMapping(value = "/listFlux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatPrompt> flux() {
        return reactiveChatPromptRepository.findByEnabled(true);
    }

    @GetMapping(value = "/list")
    public Mono<List<String>> list(String modelType) {
        return reactiveChatPromptRepository.findByEnabledAndModelType(true, modelType)
                .map(ChatPrompt::getType)
                .collectList();
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> test() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    String eventData = "SSE Event - " + sequence;
                    return ServerSentEvent.<String>builder()
                            .id(String.valueOf(sequence))
                            .data(eventData)
                            .retry(Duration.ofSeconds(2))
                            .comment("This is a comment")
                            .build();
                });

    }
}
