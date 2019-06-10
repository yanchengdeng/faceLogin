package com.baidu.aip.bean;

import java.io.Serializable;

//注册百度人脸需要的参数
public class RegisterFaceParams implements Serializable {

    public String name;//学生姓名
    public String uuid;//对应学校ic卡 卡号
    public String group;//对接校园在线saas平台的学校tid

    public RegisterFaceParams(String group, String uuid,String name) {
        this.name = name;
        this.uuid = uuid;
        this.group = group;
    }
}
