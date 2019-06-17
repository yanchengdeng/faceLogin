package com.baidu.aip.bean;

/**
*
* Author: 邓言诚  Create at : 2019-06-10  15:52
* Email: yanchengdeng@gmail.com
* Describle: 注册人脸识别信息返回模板
*/
public class RegistFaceResult  {

    private String face_oken;//唯一头像键
    private String nativeHeadPhoto;//本地存储的扫描头像


    public String getFaceToken() {
        return face_oken;
    }

    public void setFaceToken(String faceToken) {
        this.face_oken = faceToken;
    }

    public String getNativeHeadPhoto() {
        return nativeHeadPhoto;
    }

    public void setNativeHeadPhoto(String nativeHeadPhoto) {
        this.nativeHeadPhoto = nativeHeadPhoto;
    }
}
