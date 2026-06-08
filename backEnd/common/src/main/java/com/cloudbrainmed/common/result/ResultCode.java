package com.cloudbrainmed.common.result;

public interface ResultCode {
    Integer SUCCESS = 200;
    Integer PARAM_ERROR = 400;
    Integer UNAUTHORIZED = 401;
    Integer FORBIDDEN = 403;
    Integer NOT_FOUND = 404;
    Integer INTERNAL_ERROR = 500;
    Integer BUSINESS_ERROR = 501;
}