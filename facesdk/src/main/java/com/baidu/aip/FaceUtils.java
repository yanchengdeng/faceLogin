package com.baidu.aip;

import android.app.Application;

import com.baidu.aip.api.ApiConfig;
import com.baidu.aip.listeners.ScanFaceListener;
import com.baidu.idl.facesdk.FaceTracker;

/**
*
* Author: 邓言诚  Create at : 2019-06-05  10:14
* Email: yanchengdeng@gmail.com
* Describle: 脸部识别工具类 ：初始化  参数配置
*/
public class FaceUtils {

    public static ScanFaceListener scanFaceListener;

    /**
     *
     * @param faceApplictaion
     * @param groupId 分组id  默认指向saas 中 学校  tid
     * @param licenseId  申明id
     * @param licenseFileName  申明信息地址
     */
    public static void init(Application faceApplictaion,String groupId,String licenseId, String licenseFileName){
        initLib(faceApplictaion,licenseId, licenseFileName);
        ApiConfig.setGroupId(groupId);
    }

    /**
     * 初始化SDK
     */
    private static void initLib(Application faceApplictaion,String licenseId, String licenseFileName) {
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().init(faceApplictaion, licenseId, licenseFileName);
        setFaceConfig(faceApplictaion);
    }


    private static void setFaceConfig(Application faceApplictaion) {
        FaceTracker tracker = FaceSDKManager.getInstance().getFaceTracker(faceApplictaion);  //.getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
        // 模糊度范围 (0-1) 推荐小于0.7
        tracker.set_blur_thr(FaceEnvironment.VALUE_BLURNESS);
        // 光照范围 (0-1) 推荐大于40
        tracker.set_illum_thr(FaceEnvironment.VALUE_BRIGHTNESS);
        // 裁剪人脸大小
        tracker.set_cropFaceSize(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        // 人脸yaw,pitch,row 角度，范围（-45，45），推荐-15-15
        tracker.set_eulur_angle_thr(FaceEnvironment.VALUE_HEAD_PITCH, FaceEnvironment.VALUE_HEAD_ROLL,
                FaceEnvironment.VALUE_HEAD_YAW);
        // 最小检测人脸（在图片人脸能够被检测到最小值）80-200， 越小越耗性能，推荐120-200
        tracker.set_min_face_size(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        //
        tracker.set_notFace_thr(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 人脸遮挡范围 （0-1） 推荐小于0.5
        tracker.set_occlu_thr(FaceEnvironment.VALUE_OCCLUSION);
        // 是否进行质量检测
        tracker.set_isCheckQuality(true);
        // 是否进行活体校验
        tracker.set_isVerifyLive(false);
    }


    public static void setScanFaceListener(ScanFaceListener listener){
        scanFaceListener =  listener;
    }
}
