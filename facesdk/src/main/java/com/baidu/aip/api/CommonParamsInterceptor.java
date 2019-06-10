package com.baidu.aip.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * post 请求的拦截器
 */
public class CommonParamsInterceptor implements Interceptor {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request.method().equals("GET")) {
            HttpUrl.Builder builder;
            builder = request.url().newBuilder();
            if (ApiConfig.getCommonParams().size() > 0) {
                for (String key : ApiConfig.getCommonParams().keySet()) {
                    builder.addQueryParameter(key, ApiConfig.getCommonParams().get(key));
                }
            }
            request = request.newBuilder().url(builder.build()).build();
            return chain.proceed(request);
        } else if (request.method().equals("POST")){
            Map<String, String> arrayMap = new HashMap<>();



            if (request.body() instanceof FormBody) {
                FormBody oldBody = (FormBody) request.body();
                for (int i = 0; i < oldBody.size(); i++) {
                    arrayMap.put(oldBody.encodedName(i), oldBody.encodedValue(i));
                }

                if (ApiConfig.getCommonParams().size()>0){
                    for (String key:ApiConfig.getCommonParams().keySet()){
                        arrayMap.put(key,ApiConfig.getCommonParams().get(key));
                    }
                }
                request = request.newBuilder().post(oldBody).build();
            }
//            Gson gson = new Gson();
//            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(arrayMap));
//            request = request.newBuilder().post(requestBody).build();
            return chain.proceed(request);
        }else{
            return chain.proceed(request);
        }
    }
}
