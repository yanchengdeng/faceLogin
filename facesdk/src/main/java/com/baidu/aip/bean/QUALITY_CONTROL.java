package com.baidu.aip.bean;

/**
*
* Author: 邓言诚  Create at : 2019-06-10  17:06
* Email: yanchengdeng@gmail.com
* Describle: 人脸注册时  图片质量
 * NONE:不进行控制 
 * LOW:*较低的质量要求 
 * NORMAL: 一般的质量要求 
 * HIGH: 较高的质量要求
 * 默认 NONE
*/
public enum QUALITY_CONTROL {
    QUALITY_CONTROL_NONE("NONE"),
    QUALITY_CONTROL_LOW("LOW"),
    QUALITY_CONTROL_NORMAL("NORMAL"),
    QUALITY_CONTROL_HIGH("HIGH");
    private String type;
    QUALITY_CONTROL(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
