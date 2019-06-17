package com.baidu.aip.bean;

import java.util.List;

/**
 * Author: 邓言诚  Create at : 2019-06-12  16:36
 * Email: yanchengdeng@gmail.com
 * Describle: 验证人脸信息
 */

public class VertifyFaceResult {

    private String face_token;
    private List<VertifyUserInfo> user_list;

    public String getFaceToken() {
        return face_token;
    }

    public List<VertifyUserInfo> getUserList() {
        return user_list;
    }


    public class VertifyUserInfo {
        private String group_id;
        private String user_id;
        private String user_info;
        private float score;//用户的匹配得分，推荐阈值80分

        @Override
        public String toString() {
            return "VertifyUserInfo{" +
                    "group_id='" + group_id + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", user_info='" + user_info + '\'' +
                    ", score=" + score +
                    '}';
        }

        public String getGroup_id() {
            return group_id;
        }


        public String getUser_id() {
            return user_id;
        }


        public String getUser_info() {
            return user_info;
        }


        public float getScore() {
            return score;
        }

    }
}
