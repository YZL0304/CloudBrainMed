package com.cloudbrainmed.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 501;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}