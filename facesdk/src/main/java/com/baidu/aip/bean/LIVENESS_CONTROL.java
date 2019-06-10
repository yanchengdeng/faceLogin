package com.baidu.aip.bean;

/**
*
* Author: 邓言诚  Create at : 2019-06-10  17:06
* Email: yanchengdeng@gmail.com
* Describle: 人脸注册时 活体检测质量
 * NONE: 不进行控制
 * LOW:较低的活体要求(高通过率 低攻击拒绝率)
 * NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)
 * HIGH: 较高的活体要求(高攻击拒绝率 低通过率)
 * 默认NONE
*/
public enum LIVENESS_CONTROL {
    LIVENESS_CONTROL_NONE("NONE"),
    LIVENESS_CONTROL_LOW("LOW"),
    LIVENESS_CONTROL_NORMAL("NORMAL"),
    LIVENESS_CONTROL_HIGH("HIGH");
    private String type;
    LIVENESS_CONTROL(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
