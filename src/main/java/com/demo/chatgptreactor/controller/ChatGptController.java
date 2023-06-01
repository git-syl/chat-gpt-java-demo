/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.demo.chatgptreactor.controller;

import com.demo.chatgptreactor.api.ChatGptService;
import com.demo.chatgptreactor.config.ChatGptConfig;
import com.demo.chatgptreactor.dto.*;
import com.demo.chatgptreactor.po.ChatCount;
import com.demo.chatgptreactor.po.ChatPrompt;
import com.demo.chatgptreactor.repository.ReactiveChatPromptRepository;
import com.demo.chatgptreactor.repository.ReactiveChatTotalRepository;
import com.demo.chatgptreactor.utils.JtokkitUtils;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;

import static com.demo.chatgptreactor.api.ChatGptService.CONTENT;

/**
 * Created by syl nerosyl@live.com on 2023/5/15
 *
 * @author syl
 */
@Slf4j
@RestController
@RequestMapping(value = {"/chat","/v1/chat"})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ChatGptController {

    @NonNull
    ChatGptService chatGptService;

    @NonNull
    WebClient webClient;

    @NonNull
    ReactiveChatPromptRepository reactiveChatPromptRepository;

    @NonNull
    ReactiveChatTotalRepository reactiveChatTotalRepository;

    @NonNull
    ChatGptConfig chatGptConfig;



    @PostMapping(value = "/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> completions(@RequestBody @Valid ChatRequest request) {
        return chatGptService.completionsStr(new LinkedMultiValueMap<>() {{
            add("Authorization", "Bearer " + chatGptConfig.getOpenAi().getApiKey());
        }}, request);
    }

    @PostMapping(value = "/completions-web-client", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> completionsWebClient(@RequestBody @Valid ChatRequest request) {
        ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {
        };
        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(request)
                .header("Authorization", "Bearer " + chatGptConfig.getOpenAi().getApiKey())
                .retrieve()
                .bodyToFlux(type);
    }


    @PostMapping(value = "/chat-by-prompt", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, String>> chatByPromptContext(
            @RequestHeader(value = "userId", required = false) String userId,
            @RequestHeader(value = "clientId", required = false) String clientId,
            @RequestBody @Validated ChatPromptRequest chatPromptRequest) {


        if (!chatPromptRequest.isHistoryCloseValid()) {
            log.warn("contextOption=CLOSE,But history array is not empty");
        }

        Mono<String> promptByRepository = reactiveChatPromptRepository.getChatPromptByType(chatPromptRequest.getType())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid chat prompt type!")))
                .flatMap(chatPrompt -> {
                    return !chatPrompt.allowContextList()
                            .contains(chatPromptRequest.getContextOption()) ?
                            Mono.error(new IllegalArgumentException("contextOption should in ['OPEN','CLOSE'],This Type Only Allowed:" + chatPrompt.allowContextList()))
                            : Mono.just(chatPrompt.getPrompt());
                })
                .flatMap(prompt -> {
                    ExpressionParser parser = new SpelExpressionParser();
                    EvaluationContext context = new StandardEvaluationContext(chatPromptRequest);
                    String parserMsg = parser.parseExpression(
                            prompt,
                            new TemplateParserContext()).getValue(context, String.class);
                    Assert.notNull(parserMsg, "parseExpression parserMsg null");
                    return Mono.just(parserMsg);
                }).cache();

        Flux<ChatCompletionChunk> completionsSteam =
                promptByRepository
                        .flatMapMany(message -> {
                            var request = ChatRequest.createByDefaultEmpty(buildChatMessages(message, chatPromptRequest.getHistory()));
                            return chatGptService.completionsSteam(new LinkedMultiValueMap<>() {{
                                add("Authorization", "Bearer " + chatGptConfig.getOpenAi().getApiKey());
                            }}, request);

                        }).onErrorResume(e -> e instanceof DecodingException ? Flux.empty() : Flux.error(e))
                        .cache();


        Mono<ChatCount> chatCount = completionsSteam
                .map(ChatCompletionChunk::getChoices)
                .filter(m -> m != null && !m.isEmpty())
                .map(m -> m.get(0))
                .map(ChatCompletionChunk.Choice::getDelta)
                .filter(Objects::nonNull)
                .map(m -> m.getOrDefault(CONTENT, ""))
                .filter(Objects::nonNull)
                .map(JtokkitUtils::countTokens)
                .reduce(0, Integer::sum)
                .zipWith(promptByRepository)
                .flatMap(countTuple -> {
                    int completionTokens = countTuple.getT1();
                    int promptTokens = JtokkitUtils.countTokens(countTuple.getT2());
                    int tokens = completionTokens + promptTokens;
                    return reactiveChatTotalRepository.save(ChatCount.builder()
                            .completionTokens(completionTokens)
                            .promptTokens(promptTokens)
                            .tokens(tokens)
                            .createTime(LocalDateTime.now())
                            .userId(userId)
                            .build());
                });


        return completionsSteam
                .map(ChatCompletionChunk::getChoices)
                .filter(m -> m != null && !m.isEmpty())
                .map(m -> m.get(0))
                .map(ChatCompletionChunk.Choice::getDelta)
                .filter(m -> m.get(CONTENT) != null)
                .publishOn(Schedulers.boundedElastic())
                .doFinally(signalType -> {
                    chatCount.subscribe();
                });

    }


    @PostMapping(value = "/block/chat_prompt", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatCompletionNoSteam> blockChatPrompt(
            @RequestHeader(value = "clientId") String clientId,
            @RequestHeader(value = "userId") String userId,
            @RequestBody @Validated ChatPromptRequest chatPromptRequest) {

        Mono<ChatCompletionNoSteam> completionsSteam =
                reactiveChatPromptRepository.getChatPromptByType(chatPromptRequest.getType())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid chat prompt type!")))
                        .map(ChatPrompt::getPrompt)
                        .flatMap(prompt -> {
                            ExpressionParser parser = new SpelExpressionParser();
                            EvaluationContext context = new StandardEvaluationContext(chatPromptRequest);
                            String value = parser.parseExpression(
                                    prompt,
                                    new TemplateParserContext()).getValue(context, String.class);
                            assert value != null;
                            return Mono.just(value);
                        })
                        .flatMap(m -> {
                            var request = ChatRequest.createByDefault(m, false);
                            return chatGptService.completionsBlock(new LinkedMultiValueMap<>() {{
                                add("Authorization", "Bearer " + chatGptConfig.getOpenAi().getApiKey());
                            }}, request);
                        });


        Mono<ChatCount> chatCount = completionsSteam
                .map(ChatCompletionNoSteam::getUsage)
                .map(ChatCompletionNoSteam.UsageDTO::getTotalTokens)
                .flatMap(count -> {
                    ChatCount entity = new ChatCount();
                    entity.setUserId(userId);
                    entity.setTokens(count);
                    entity.setCreateTime(LocalDateTime.now());
                    return reactiveChatTotalRepository.save(entity);
                });


        return chatCount.then(completionsSteam);

    }


    //TODO: Performance optimization
    private List<Message> buildChatMessages(String currentMessage, List<Message> history) {

        Deque<Message> chatMessagesStack = new ArrayDeque<>();

        chatMessagesStack.push(Message.create(currentMessage));

        int maxTokens = chatGptConfig.getOpenAi().getMaxTokens();
        int initSystemPromptTokens = chatGptConfig.getOpenAi().getInitSystemPromptTokens();

        int curTokens = initSystemPromptTokens + JtokkitUtils.countTokens(currentMessage);
        for (int i = history.size() - 1; i >= 0; i--) {
            Message message = history.get(i);
            int messageTokens = JtokkitUtils.countTokens(message.getContent());
            if (curTokens + messageTokens <= maxTokens) {
                curTokens += messageTokens;
                chatMessagesStack.push(message);
            } else {
                break;
            }
        }

        return new ArrayList<>(chatMessagesStack);
    }


}
