package com.demo.chatgptreactor.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 返回实体
 *
 * @author syl
@since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResultOld<T>  extends Result<T> {

    @JsonProperty("code")
    @Override
    public Integer getCode() {
        return super.getCode();
    }

    @JsonIgnore
    @Override
    public String getErrorCode() {
        return super.getErrorCode();
    }

    public ResultOld(Integer code, String message, T data) {
        super(code, message, data);
    }

    public ResultOld(Integer code, String errorCode, String message, T data) {
        super(code, errorCode, message, data);
    }

    public static ResultOld<Object> ok() {
        return new ResultOld<>(OLD_CODE_SUCCESS, "", null);
    }

    public static ResultOld<Object> ok(String message) {
        return new ResultOld<>(OLD_CODE_SUCCESS, message, null);
    }

    public static <T> ResultOld<T> success(T data) {
        return new ResultOld<>(OLD_CODE_SUCCESS, MESSAGE_SUCCESS, data);
    }

    public static <T> ResultOld<T> success(T data, String message) {
        return new ResultOld<>(OLD_CODE_SUCCESS, message, data);
    }


    public static ResultOld<Object> error( String message) {
        return ResultOld.error(CODE_SYSTEM_ERROR, message, null);
    }

    public static ResultOld<Object> error(Integer code,  String message, Object data) {
        return new ResultOld<>(code, message, data);
    }


}


