package org.example.backend.common;

public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(String msg) {
        this(400, msg);
    }

    public Integer getCode() { return code; }
}
