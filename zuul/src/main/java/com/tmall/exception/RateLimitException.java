package com.tmall.exception;

/**
 * exception
 * Created by liufeng
 * 2019-05-20 23:44
 */
public class RateLimitException extends RuntimeException {
    private String msg;

    public RateLimitException(){}

    public RateLimitException(String msg){
        this.msg = msg;
    }
}
