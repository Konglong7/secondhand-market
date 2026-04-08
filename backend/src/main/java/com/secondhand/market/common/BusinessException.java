package com.secondhand.market.common;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {
    /** 错误码 */
    private Integer code = 500;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}