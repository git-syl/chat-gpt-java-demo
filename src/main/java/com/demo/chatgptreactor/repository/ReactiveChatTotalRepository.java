package com.demo.chatgptreactor.repository;

import com.demo.chatgptreactor.po.ChatCount;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface ReactiveChatTotalRepository extends ReactiveCrudRepository<ChatCount, Long> {

  //Mono<ChatCount> getByReplaceId(String replaceId);
}