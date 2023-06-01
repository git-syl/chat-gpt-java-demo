package com.demo.chatgptreactor.po;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Created by syl nerosyl@live.com on 2023/5/23
 *
 * @author syl
 */
@Accessors(chain = true)
@Table(value = "chat_count")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCount {


    @Id
    private Long id;

    /**
     * total_count
     */
    @Column(value = "tokens")
    private Integer tokens;

    @Column("prompt_tokens")
    private Integer promptTokens;
    @Column("completion_tokens")
    private Integer completionTokens;

    @Column(value = "user_id")
    private String userId;

    @Column(value = "create_time")
    private LocalDateTime createTime;
}
