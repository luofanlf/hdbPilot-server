package com.iss.hdbPilot.common;

/**
 * 结果工具类，用于快速构建 BaseResponse 对象
 */
public class ResultUtils {

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     */
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(-1, null, message);
    }
}