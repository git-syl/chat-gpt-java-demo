package com.demo.chatgptreactor.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Table(value = "chat_prompt")
public class ChatPrompt {

    @Id
    private Long id;


    private String allowContext;

    @ReadOnlyProperty
    public List<String> allowContextList(){
        if (this.getAllowContext()==null){
            return Collections.emptyList();
        }
        return Arrays.asList(this.getAllowContext().split(","));
    }



    private String type;


    private String prompt;

    private LocalDateTime createTime;
    private boolean enabled;
    private String modelType;


}
