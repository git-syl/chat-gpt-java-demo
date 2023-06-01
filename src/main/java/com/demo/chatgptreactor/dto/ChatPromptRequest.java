package com.demo.chatgptreactor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatPromptRequest {

    @NotBlank
    private String type;

    /**
     * not full content just params
     */
    private Map<String, String> content;

    private List<Message> history = new ArrayList<>();

    private String contextOption;

    public boolean isHistoryCloseValid(){
        return !"CLOSE".equals(contextOption) || history.isEmpty();
    }

}
