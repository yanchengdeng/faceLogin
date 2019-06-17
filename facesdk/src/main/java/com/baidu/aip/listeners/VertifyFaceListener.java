package com.baidu.aip.listeners;


import com.baidu.aip.bean.VertifyFaceResult;

/**
*
* Author: 邓言诚  Create at : 2019-06-10  12:00
* Email: yanchengdeng@gmail.com
* Describle: 人脸认证返回回调监听
*/
public interface VertifyFaceListener {

     void onSucess(VertifyFaceResult result);

     void onFaild(int code, String message);
}
