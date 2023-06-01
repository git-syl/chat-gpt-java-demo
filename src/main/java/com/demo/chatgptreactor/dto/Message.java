package com.demo.chatgptreactor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by syl nerosyl@live.com on 2023/5/25
 *
 * @author syl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;

    public static Message create(String content){
        return new Message("user",content);
    }

    public static Message create(String role,String content){
        return new Message(role,content);
    }

}
