package com.demo.chatgptreactor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by syl nerosyl@live.com on 2023/5/25
 *
 * @author syl
 */
@NoArgsConstructor
@Data
public class DeltaDto {

    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;
}
