package com.demo.chatgptreactor.hanlder;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by syl nerosyl@live.com on 2023/1/10
 *
 * @author syl
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    /**
     * http-status
     */
    private int status;


    private String errorText;

    /**
     * 对应error-code  详细错误信息
     */
    private String errorCode = "SERVICE_ERROR";



    public ServiceException(Integer status, String errorCode, String errorText) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    public ServiceException(Integer status, String errorText) {
        this.status = status;
        this.errorText = errorText;
    }


    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "status=" + status +
                ", errorCode='" + errorCode + '\'' +
                ", errorText='" + errorText + '\'' +
                '}';
    }
}
