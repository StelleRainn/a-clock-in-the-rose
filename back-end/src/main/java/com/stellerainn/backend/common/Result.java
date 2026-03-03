package com.stellerainn.backend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 200 success, 500 error
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(msg);
        return result;
    }
}
