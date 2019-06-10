package com.baidu.aip.api;

//接口静态类

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 接口请求
 */
public class API {


    /**
     * 简单数据请求
     *
     * @param call
     * @param apiCallBack
     */
    public static<T> void getData(Call<ResponseBody> call, final Class clz, final ApiCallBack<T> apiCallBack) {
        if (call == null) {
            throw new IllegalArgumentException("call不能为空");
        }
        if (apiCallBack == null) {
            throw new IllegalArgumentException("缺少ApiCallBack()回调");
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showRequestParams(response);
                if (apiCallBack != null) {
                    if (response.isSuccessful()) {
                        try {
                            if (response.body() == null) {
                                return;
                            }
                            apiCallBack.onSuccess(response.code(), response.message() + "", (T) new Gson().fromJson(response.body().string(),clz));
                        } catch (IOException e) {
                            e.printStackTrace();
                            apiCallBack.onFaild(-1, "数据解析失败");
                        }
                    } else {
                        apiCallBack.onFaild(response.code(), response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showErroResponse(call, t);
                if (NetworkUtils.isConnected()) {
                    apiCallBack.onFaild(-1, t.getMessage());
                } else {
                    apiCallBack.onFaild(-1, "请检查网络");
                }
            }
        });
    }

    /**
     * 满足百度接口数据格式实体对象请求
     *
     * @param call
     * @param clz
     * @param apiCallBack
     * @param <T>
     */
    public static <T> void getBaiduObject(Call<ResultInfo> call, final Class clz, final ApiCallBack apiCallBack) {
        if (call == null) {
            throw new IllegalArgumentException("call不能为空");
        }

        if (apiCallBack == null) {
            throw new IllegalArgumentException("缺少ApiCallBack()回调");
        }
        call.enqueue(new Callback<ResultInfo>() {
            @Override
            public void onResponse(Call<ResultInfo> call, Response<ResultInfo> response) {
                showRequestParams(response);
                if (apiCallBack != null) {
                    ResultInfo resultInfo = response.body();
                    if (resultInfo != null) {
                        if (resultInfo.getStatus_code() == ApiConfig.successCode) {
                            //查询成功
                            if (resultInfo.getData() != null) {
                                try {
                                    //防止服务器中的字符串不严谨 ，这做一次转化 解决：com.google.gson.stream.MalformedJsonException
                                    String json = new Gson().toJson(resultInfo.getData());
                                    apiCallBack.onSuccess(resultInfo.getStatus_code(), resultInfo.getMessage(), (T) new Gson().fromJson(json, clz));
                                } catch (Exception e) {
                                    LogUtils.w("dyc", "json 数据格式异常");
                                    apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage() + ":json 数据格式异常");
                                }
                            } else {
                                apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage() + ":data 无数据");
                            }
                        } else {
                            apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage());
                        }
                    } else {
                        if (response != null) {
                            showResponseErrorInfo(apiCallBack, response);
                        }
                        apiCallBack.onFaild(response.code(), "" + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultInfo> call, Throwable t) {
                showErroResponse(call, t);
                if (NetworkUtils.isConnected()) {
                    apiCallBack.onFaild(-1, t.getMessage());
                } else {
                    apiCallBack.onFaild(-1, "请检查网络");
                }
            }
        });
    }


    /**
     * 打印响应错误信息
     *
     * @param call
     * @param t
     */
    private static void showErroResponse(Call call, Throwable t) {
        if (ApiConfig.getDebug()) {
            Log.d(ApiConfig.LogFilter, call.toString() + "--error：" + t.getMessage());
        }
    }


    /**
     * 打印请求信息 ， 接口返回结果
     *
     * @param response
     */
    private static void showRequestParams(Response response) {
        if (response == null) {
            Log.e(ApiConfig.LogFilter, "response返回响应值为空");
            return;
        }

        if (ApiConfig.getDebug()) {
            if (response.raw() != null) {
                if (response.raw().request().body() != null) {
                    RequestBody body = response.raw().request().body();
                    Buffer requestBuffer = new Buffer();
                    try {
                        body.writeTo(requestBuffer);
//                        Log.w(ApiConfig.getLogFilter(), String.format("接口请求:url---->  %s", response.raw().request().url().toString() + postParseParams(body, requestBuffer)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 响应错误信息
     */
    private static void showResponseErrorInfo(ApiCallBack apiCallBack, Response response) {
        if (response.body() != null) {
            if (ApiConfig.getDebug()) {
                Log.e(ApiConfig.LogFilter, String.format("接口返回信息：code-->%s message:-->%s", response.code(), response.message()));
            }
        }
        if (NetworkUtils.isConnected()) {
            apiCallBack.onFaild(response.code(), response.message());
        } else {
            apiCallBack.onFaild(response.code(), "请检查网络");
        }
    }


    /**
     * gson字符串转化成集合
     *
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonStringConvertToList(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (ApiConfig.getDebug()) {
//            Log.w(ApiConfig.LogFilter, String.format("接口返回结果：%s", jsonString));
//        }
        return list;
    }


    private static String postParseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8");
        }
        return "null";
    }
}
