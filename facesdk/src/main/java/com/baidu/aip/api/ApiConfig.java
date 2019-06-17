package com.baidu.aip.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.baidu.aip.bean.LIVENESS_CONTROL;
import com.baidu.aip.bean.QUALITY_CONTROL;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
*
* Author: 邓言诚  Create at : 2019-06-05  11:00
* Email: yanchengdeng@gmail.com
* Describle: 网络请求配置
*/
public class ApiConfig {


    private volatile static Retrofit retrofit;

    private static String API_HOST = "git/";

    public static int successCode = 0;

    //设置日志过滤关键字
    public static String LogFilter = "dyc";
    //日志打印
    private static Boolean isDebug = true;
    //请求连接时长
    private static int connectTimeout = 10_000;
    //读写时长
    private static int readTimeout = 10_000;
    private static int writeTimeout = 10_000;

    public static Context context;

    //headers 公共参数
    private static HashMap<String, String> headersParams = new HashMap<>();

    //api 请求公共参数
    private static HashMap<String, String> commonParams = new HashMap<>();

    //访问令牌
    public static String accessToken;

    //分组   目前已学校为组设计  默认分组 到0   根据saas  学校tid 是从1开始 如果未设置，则分组到 0 默认组
    private static String groupId = "0";


    public static String livenessControl = "NONE";//活体检查

    public static String qualityControl = "NONE";//图片质量  目前使用场景不存在


    public static String getLivenessControl() {
        return livenessControl;
    }

    /**
     * 设置人脸活体检查质量类型
     * @param livenessControl
     */
    public static void setLivenessControl(LIVENESS_CONTROL livenessControl) {
        ApiConfig.livenessControl = livenessControl.getType();
    }


    public static String getQualityControl() {
        return qualityControl;
    }

    /**
     * 设置人脸活体检测面部图片质量类型
     * @param qualityControl
     */
    public static void setQualityControl(QUALITY_CONTROL qualityControl) {
        ApiConfig.qualityControl = qualityControl.getType();
    }

    /**
     * 添加头请求公共参数
     *
     * @param key
     * @param value
     */
    public static void addHeadersParams(String key, String value) {
        headersParams.put(key, value);
    }

    /**
     * 添加头请求公共参数
     *
     * @param maps
     */
    public static void addHeadersParams(HashMap<String, String> maps) {
        headersParams.putAll(maps);
    }

    /**
     * 添加公共参数
     *
     * @param key
     * @param value
     */
    public static void addCommonParams(String key, String value) {
        commonParams.put(key, value);
    }

    /**
     * 添加公共参数
     *
     * @param maps
     */
    public static void addCommonParams(HashMap<String, String> maps) {
        commonParams.putAll(maps);
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessTokenParas) {
        accessToken = accessTokenParas;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String groupIdParas) {
        groupId = groupIdParas;
    }

    //TODO  预留  此处可以更改api 返回结果结构！！！！ 待开发
    public static void setRequestStruct(String codeName, String msgName, String resultName) {
        ResultInfo resultInfo = new ResultInfo();
        Field[] fields = resultInfo.getClass().getFields();
    }


    public static void setSuccessCode(int code){
        successCode = code;
    }

    public static Boolean getDebug() {
        return isDebug;
    }

    public static void setDebug(Boolean isDebug) {
        ApiConfig.isDebug = isDebug;
    }

    public static String getLogFilter() {
        return LogFilter;
    }

    public static void setLogFilter(String logFilter) {
        LogFilter = logFilter;
    }

    public static String getApiUrl() {
        return API_HOST;
    }



    public static HashMap<String, String> getCommonParams() {
        return commonParams;
    }

    public static void setCommonParams(HashMap<String, String> commonParams) {
        ApiConfig.commonParams = commonParams;
    }

    public static HashMap<String, String> getHeadersParams() {
        return headersParams;
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(int connectTimeout) {
        ApiConfig.connectTimeout = connectTimeout;
    }

    public static int getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(int readTimeout) {
        ApiConfig.readTimeout = readTimeout;
    }

    public static int getWriteTimeout() {
        return writeTimeout;
    }

    public static void setWriteTimeout(int writeTimeout) {
        ApiConfig.writeTimeout = writeTimeout;
    }

    public static void init(Application context, String host) {
        init(context, host, false);
    }

    public static void init(Application context, String host, HashMap<String, String> headersParams) {
        init(context, host, false);
        setHeadersParams(headersParams);
    }

    public static void init(Application context, String host, boolean isNeedHttps) {
        API_HOST = (isNeedHttps ? "https://" : "http://") + host + "/";
        init(context, host, isNeedHttps, null);
    }

    public static void init(Application context, String host, boolean isNeedHttps, HashMap<String, String> headersParams) {
        API_HOST = (isNeedHttps ? "https://" : "http://") + host + "/";
        setHeadersParams(headersParams);
        initContext(context);
    }

    private static void initContext(Application application) {
        context = application.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (activity.getParent() != null) {
                    ApiConfig.context = activity.getParent();
                } else {
                    ApiConfig.context = activity;
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //可以全局做网络监听、activity栈的保存 溢出 或是只类似商品详情只出现一个实例等等
                if (activity.getParent() != null) {
                    ApiConfig.context = activity.getParent();
                } else {
                    ApiConfig.context = activity;
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity.getParent() != null) {
                    ApiConfig.context = activity.getParent();
                } else {
                    ApiConfig.context = activity;
                }

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    private static void setHeadersParams(HashMap<String, String> headersParams) {
        if (headersParams != null && !headersParams.isEmpty()) {
            ApiConfig.headersParams = headersParams;
        }
    }

    @NonNull
    public static Retrofit getInstants() {
        if (retrofit == null) {
            synchronized (API.class) {
                if (retrofit == null) {

                    // 指定缓存路径,缓存大小 50Mb
                    Cache cache = new Cache(new File(ApiConfig.context.getCacheDir(), "HttpCache"),
                            1024 * 1024 * 50);
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                            .cookieJar(cookieJar)
                            .cache(cache)
//                            .addInterceptor(new HeadersInterceptor())
                            .addInterceptor(new CommonParamsInterceptor())
//                            .addInterceptor(new CacheInterceptor())
                            .addInterceptor(new LogInterceptor())
//                            .addInterceptor(new EncryptInterceptor())
                            .connectTimeout(getConnectTimeout(), TimeUnit.SECONDS)
                            .readTimeout(getReadTimeout(), TimeUnit.SECONDS)
                            .writeTimeout(getWriteTimeout(), TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true);

                    retrofit = new Retrofit.Builder()
                            .baseUrl(getApiUrl())
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }

        return retrofit;
    }


}
