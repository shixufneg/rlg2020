package com.itdr.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itdr.config.ConstCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
//    状态码
    private Integer status;
//    数据
    private T data;
//    错误信息
    private String msg;

    private ServerResponse(Integer status) {
        this.status = status;
    }

    private ServerResponse(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(Integer status, T data, String msg) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    private ServerResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(String msg) {
        this.status = ConstCode.DEFAULT_FAIL;
        this.msg = msg;
    }


    public static <T> ServerResponse successRS() {
        return new ServerResponse(ConstCode.DEFAULT_SUCCESS);
    }

    public static <T> ServerResponse successRS(String msg) {
        return new ServerResponse(ConstCode.DEFAULT_SUCCESS,msg);
    }
    public static <T> ServerResponse successRS(Integer status,T data) {
        return new ServerResponse(status,data);
    }


    public static <T> ServerResponse successRS(T data) {
        return new ServerResponse(ConstCode.DEFAULT_SUCCESS,data);
    }

    //    成功的时候传入状态码、数据、信息
    public static <T> ServerResponse successRS(Integer status,T data, String msg) {
        return new ServerResponse(status,data, msg);
    }

    public static <T> ServerResponse defeatedRS(Integer errorCode, String errorMessage) {
        return new ServerResponse(ConstCode.DEFAULT_FAIL, errorMessage);
    }

    public static <T> ServerResponse defeatedRS(String errorMessage) {
        return new ServerResponse(ConstCode.DEFAULT_FAIL,errorMessage);
    }

    @JsonIgnore
//让success不在json序列化结果之中
    public boolean isSuccess() {
        return this.status == ConstCode.DEFAULT_SUCCESS;
    }

}