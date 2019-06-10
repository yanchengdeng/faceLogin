package com.baidu.aip.api;
/**
*
* Author: 邓言诚  Create at : 2019-06-05  11:16
* Email: yanchengdeng@gmail.com
* Describle: 接口回调申明
*/
public interface ApiCallBack<T> {

    void onSuccess(int msgCode, String msg, T data);


    void onFaild(int msgCode, String msg);
}
