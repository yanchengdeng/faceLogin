package com.baidu.aip.api;

import android.text.TextUtils;

import com.baidu.aip.bean.AccessToken;
import com.baidu.aip.utils.SysUtils;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019-06-05  14:09
 * Email: yanchengdeng@gmail.com
 * Describle: 测试api
 */
public class ApiTest {


    /**
     * 获取百度人脸识别  accessToken 信息
     * 只是测试阶段，该功能 建议放到服务端处理
     *
     * @param clientID
     * @param secret
     */
    public static void getToken(String clientID, String secret) {
        Call<ResponseBody> call = ApiConfig.getInstants().create(ApiService.class).getAccessToken(clientID, secret);
        API.getData(call, AccessToken.class, new ApiCallBack<AccessToken>() {
            @Override
            public void onSuccess(int msgCode, String msg, AccessToken accessToken) {
                if (accessToken != null && !TextUtils.isEmpty(accessToken.access_token)) {
                    ApiConfig.setAccessToken(accessToken.access_token);
                    SysUtils.log("accessToken 获取成功：");
                    HashMap<String,String> maps = new HashMap<>();
                    maps.put("access_token",accessToken.access_token);
                    ApiConfig.setCommonParams(maps);

                }
            }
            @Override
            public void onFaild(int msgCode, String msg) {
                SysUtils.log("token获取失败");
            }
        });
    }


}
