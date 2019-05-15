package com.liufeng.BaseResponse;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * create by liufeng 2019 5.15
 * @param <T>
 */
@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候,如果是null的对象,key也会消失
public class ServerResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private int status;
    private String msg;
    private T data;
    private T msgData;

    private ServerResponse(String msg){
    	this.msg = msg;
    }
    
    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    
    private ServerResponse(int status,String msg,T data,T msgData){
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.msgData = msgData;
    }

    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }
    public T getMsgData(){
        return msgData;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    
    public static <T> ServerResponse<T> createBySuccess(T data,T status){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),null,data,status);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data,T msgData){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data,msgData);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    
    public static <T> ServerResponse<T> createByError(String msg,T data){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

    public static <T> ServerResponse<T> createByError(String msg){
    	return new ServerResponse<T>(msg);
    }
}
