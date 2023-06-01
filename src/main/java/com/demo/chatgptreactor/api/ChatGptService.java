package com.demo.chatgptreactor.api;


import com.demo.chatgptreactor.dto.ChatCompletionChunk;
import com.demo.chatgptreactor.dto.ChatCompletionNoSteam;
import com.demo.chatgptreactor.dto.ChatRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author syl  Date: 2023/5/15 Email:nerosyl@live.com
 */
public interface ChatGptService {

    String STEAM_END = "[DONE]";
    String CONTENT = "content";

    @PostExchange("/v1/chat/completions")
    Flux<ChatCompletionChunk> completionsSteam(@RequestHeader MultiValueMap<String, String> multiValue, @RequestBody ChatRequest request);


    @PostExchange("/v1/chat/completions")
    Flux<String> completionsStr(@RequestHeader MultiValueMap<String, String> multiValue, @RequestBody ChatRequest request);


    @PostExchange("/v1/chat/completions")
    Mono<ChatCompletionNoSteam> completionsBlock(@RequestHeader MultiValueMap<String, String> multiValue, @RequestBody ChatRequest request);


    //Flux<ServerSentEvent<ChatCompletionChunk>> completionsSteam(@RequestHeader MultiValueMap<String, String> multiValue, @RequestBody ChatRequest request);


    // Flux<ServerSentEvent<String>> completionsSteam(@RequestHeader MultiValueMap<String, String> multiValue, @RequestBody ChatRequest request);

}
