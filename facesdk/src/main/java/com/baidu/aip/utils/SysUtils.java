package com.baidu.aip.utils;

import android.os.Build;

import com.baidu.aip.api.ApiConfig;
import com.blankj.utilcode.util.LogUtils;

public class SysUtils {


    /**
     *
     * @return  true ： 系统版本 >6.0  需要做权限验证
     */
    public static boolean isCheckPemission(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }


    /**
     * 打印日志
     * @param tag
     * @param message
     */
    public static  void log(String tag,String message){
        if (ApiConfig.getDebug()){
            LogUtils.w(tag,message);
        }
    }
    /**
     * 打印日志
     * @param message
     */
    public static  void log(String message){
        if (ApiConfig.getDebug()){
           log(ApiConfig.getLogFilter(),message);
        }
    }
}
