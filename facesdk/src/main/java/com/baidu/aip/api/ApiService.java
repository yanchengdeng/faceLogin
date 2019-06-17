package com.baidu.aip.api;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author: 邓言诚  Create at : 2019-06-05  11:15
 * Email: yanchengdeng@gmail.com
 * Describle: 接口请求地址 参数 配置
 */
public interface ApiService {


    /**
     * 获取访问令牌
     *
     * @param client_id
     * @param client_secret
     * @return
     */
    @FormUrlEncoded
    @POST("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials")
    Call<ResponseBody> getAccessToken(@Field("client_id") String client_id, @Field("client_secret") String client_secret);


    /**
     * 注册人脸信息
     * uid	：是	string	用户id（由数字、字母、下划线组成），长度限制128B。
     * group_id：	是	string	用户组id，标识一组用户（由数字、字母、下划线组成），长度限制128B。如果需要将一个uid注册到多个group下，group_id需要用多个逗号分隔，每个group_id长度限制为48个英文字符。注：group无需单独创建，注册用户时则会自动创建group。
     * 产品建议：根据您的业务需求，可以将需要注册的用户，按照业务划分，分配到不同的group下，例如按照会员手机尾号作为groupid，用于刷脸支付、会员计费消费等，这样可以尽可能控制每个group下的用户数与人脸数，提升检索的准确率
     * image	：是	string	base64编码后的图片数据，需urlencode，每次只支持单张图片，编码后的图片大小不超过10M。为保证后续识别的效果较佳，建议注册的人脸，为用户正面人脸，且保持人脸都在图片之内
     * user_info：用户资料，长度限制256B
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add")
    Call<ResultInfo> register(@FieldMap() HashMap<String, String> params);


    /**
     * 人脸验证
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("https://aip.baidubce.com/rest/2.0/face/v3/search")
    Call<ResultInfo> vertify(@FieldMap() HashMap<String, String> params);


}