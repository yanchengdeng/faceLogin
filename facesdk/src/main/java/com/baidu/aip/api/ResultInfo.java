package com.baidu.aip.api;

import java.io.Serializable;
/**
*
* Author: 邓言诚  Create at : 2019-06-05  11:16
* Email: yanchengdeng@gmail.com
* Describle: 百度人脸识别接口数据模板
*/
public class ResultInfo<T> implements Serializable {


    private int status_code = 0;// 10001
    private String reason = "请求成功";//
    private T result;// null,
//    private long log_id;
//    private long timestamp;


    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return reason;
    }

    public void setMessage(String message) {
        this.reason = message;
    }

    public T getData() {
        return result;
    }

    public void setData(T data) {
        this.result = data;
    }
}
