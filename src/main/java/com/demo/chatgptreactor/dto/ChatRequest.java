package com.demo.chatgptreactor.dto;

import com.demo.chatgptreactor.config.ChatGptConfig;
import com.demo.chatgptreactor.utils.SpringContextHolder;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    @NotBlank
    private String model;
    private List<Message> messages;
    private double temperature;
    private boolean stream = true;


    // getters and setters

    public static ChatRequest createByDefault(String content) {
        return ChatRequest.createByDefault(content, true);
    }

    public static ChatRequest createByDefaultEmpty() {
        return ChatRequest.createByDefault(null, true);
    }

    public static ChatRequest createByDefaultEmpty(  List<Message> message) {
        ChatRequest byDefault = ChatRequest.createByDefault(null, true);
        byDefault.getMessages().addAll(message);
        return byDefault;
    }

    public static ChatRequest createByDefault(String userContent, boolean steam) {
        ChatRequest request = new ChatRequest();

        request.setModel("gpt-3.5-turbo");

        List<Message> messageList = new ArrayList<>();


        messageList.add(Message.create("system", SpringContextHolder.getBean(ChatGptConfig.class).getOpenAi().getSystemPrompt()));

        if (userContent != null) {
            messageList.add(Message.create(userContent));
        }

        request.setMessages(messageList);
        request.setTemperature(0.7);
        request.setStream(steam);

        return request;
    }
}
