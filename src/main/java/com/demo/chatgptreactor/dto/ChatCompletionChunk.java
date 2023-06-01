package com.demo.chatgptreactor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionChunk {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Choice {
        private Map<String, String> delta;
        private int index;
        private String finishReason;

    }


//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class Delta {
//        private String content;
//
//
//        // getters and setters
//    }
}
