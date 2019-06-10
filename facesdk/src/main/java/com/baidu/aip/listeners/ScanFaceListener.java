package com.baidu.aip.listeners;

import com.baidu.aip.bean.RegistFaceResult;

/**
*
* Author: 邓言诚  Create at : 2019-06-10  12:00
* Email: yanchengdeng@gmail.com
* Describle: 扫描面部返回回调监听
*/
public interface ScanFaceListener {

     void onSucess(RegistFaceResult result);

     void onFaild(int code,String message);
}
