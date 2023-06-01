package com.demo.chatgptreactor.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode="SERVICE_ERROR";
    private String errorText;
    private StackTraceElement[] stackTrace;
}
