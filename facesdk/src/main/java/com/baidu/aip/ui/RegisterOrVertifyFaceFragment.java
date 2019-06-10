package com.baidu.aip.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.aip.Constants;
import com.baidu.aip.FaceSDKManager;
import com.baidu.aip.FaceUtils;
import com.baidu.aip.ImageFrame;
import com.baidu.aip.api.API;
import com.baidu.aip.api.ApiCallBack;
import com.baidu.aip.api.ApiConfig;
import com.baidu.aip.api.ApiService;
import com.baidu.aip.api.ResultInfo;
import com.baidu.aip.bean.AccessToken;
import com.baidu.aip.bean.FUNCTION_TYPE;
import com.baidu.aip.bean.RegistFaceResult;
import com.baidu.aip.bean.RegisterFaceParams;
import com.baidu.aip.face.CameraImageSource;
import com.baidu.aip.face.DetectRegionProcessor;
import com.baidu.aip.face.FaceDetectManager;
import com.baidu.aip.face.FaceFilter;
import com.baidu.aip.face.PreviewView;
import com.baidu.aip.face.camera.ICameraControl;
import com.baidu.aip.utils.BrightnessTools;
import com.baidu.aip.utils.ImageSaveUtil;
import com.baidu.aip.utils.SysUtils;
import com.baidu.aip.widget.FaceRoundView;
import com.baidu.aip.widget.WaveHelper;
import com.baidu.aip.widget.WaveView;
import com.baidu.idl.facesdk.FaceInfo;
import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import demo.face.aip.baidu.com.facesdk.R;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019-06-06  10:42
 * Email: yanchengdeng@gmail.com
 * Describle: 注册 或者  认证  脸部识别功能   根据传递的 参数 FUNCTION_TYPE  决定
 */
public class RegisterOrVertifyFaceFragment extends Fragment {

    public RegisterFaceParams registerFaceParams;
    public FUNCTION_TYPE functionType;


    private static final int MSG_INITVIEW = 1001;
    private static final int MSG_BEGIN_DETECT = 1002;
    private TextView nameTextView;
    private PreviewView previewView;
    private View mInitView;
    private FaceRoundView rectView;
    private boolean mGoodDetect = false;
    private static final double ANGLE = 15;
    private boolean mDetectStoped = false;
    private ImageView mSuccessView;
    private Handler mHandler;
    private String mCurTips;
    private long mLastTipsTime = 0;
    private int mCurFaceId = -1;

    private FaceDetectManager faceDetectManager;
    private DetectRegionProcessor cropProcessor = new DetectRegionProcessor();
    private WaveHelper mWaveHelper;
    private WaveView mWaveview;
    private int mScreenW;
    private int mScreenH;
    private boolean mSavedBmp = false;
    // 开始人脸检测
    private boolean mBeginDetect = false;
    private RelativeLayout rootView;
    private View view;


    public RegisterOrVertifyFaceFragment() {
    }

    /**
     * 如果是注册  则需要传递注册信息参数
     *
     * @param functionType       人脸功能类型  注册   验证
     * @param registerFaceParams 注册参数
     * @return
     */
    public static RegisterOrVertifyFaceFragment newInstance(FUNCTION_TYPE functionType, RegisterFaceParams registerFaceParams) {
        RegisterOrVertifyFaceFragment fragment = new RegisterOrVertifyFaceFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.PASS_OBJECT, registerFaceParams);
        args.putSerializable(Constants.PASS_STRING, functionType);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 如果是验证  可以不用传
     *
     * @param function_type 人脸功能类型  注册   验证
     * @return
     */
    public static RegisterOrVertifyFaceFragment newInstance(FUNCTION_TYPE function_type) {
        RegisterOrVertifyFaceFragment fragment = new RegisterOrVertifyFaceFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.PASS_STRING, function_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            functionType = (FUNCTION_TYPE) getArguments().getSerializable(Constants.PASS_STRING);
            if (functionType == FUNCTION_TYPE.FUNCTION_TYPE_REGISTER) {
                registerFaceParams = (RegisterFaceParams) getArguments().getSerializable(Constants.PASS_OBJECT);
                if (registerFaceParams == null) {
                    LogUtils.w(RegisterOrVertifyFaceFragment.class.getName(), "注册类型 ，注册信息不能为空");
                    return;
                }
                if (TextUtils.isEmpty(registerFaceParams.group)) {
                    registerFaceParams.group = ApiConfig.getGroupId();
                }
                if (TextUtils.isEmpty(registerFaceParams.group) || TextUtils.isEmpty(registerFaceParams.name) || TextUtils.isEmpty(registerFaceParams.uuid)) {
                    LogUtils.w(RegisterOrVertifyFaceFragment.class.getName(), "注册类型 ，注册信息 分组 、姓名、uuid 不能为空");
                    return;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_face_register_or_vertify, container, false);
        init();
        initFaceView(view);
        return view;
    }

    private void initFaceView(View view) {
        faceDetectManager = new FaceDetectManager(getActivity());
        initScreen();
        initView(view);
        mHandler = new InnerHandler(getActivity());
        mHandler.sendEmptyMessageDelayed(MSG_INITVIEW, 500);
        mHandler.sendEmptyMessageDelayed(MSG_BEGIN_DETECT, 500);
    }

    private void initView(View view) {
        mInitView = view.findViewById(R.id.camera_layout);
        previewView = (PreviewView) view.findViewById(R.id.preview_view);

        rectView = (FaceRoundView) view.findViewById(R.id.rect_view);
        rootView = (RelativeLayout) view.findViewById(R.id.root_view);
        rectView = (FaceRoundView) view.findViewById(R.id.rect_view);
        CameraImageSource cameraImageSource = new CameraImageSource(getActivity());
        cameraImageSource.setPreviewView(previewView);

        faceDetectManager.setImageSource(cameraImageSource);
        faceDetectManager.setOnFaceDetectListener(new FaceDetectManager.OnFaceDetectListener() {
            @Override
            public void onDetectFace(final int retCode, FaceInfo[] infos, ImageFrame frame) {
                String str = "";
                if (retCode == 0) {
                    if (infos != null && infos[0] != null) {
                        FaceInfo info = infos[0];
                        boolean distance = false;
                        if (info != null && frame != null) {
                            if (info.mWidth >= (0.9 * frame.getWidth())) {
                                distance = false;
                                str = getResources().getString(R.string.detect_zoom_out);
                            } else if (info.mWidth <= 0.4 * frame.getWidth()) {
                                distance = false;
                                str = getResources().getString(R.string.detect_zoom_in);
                            } else {
                                distance = true;
                            }
                        }
                        boolean headUpDown;
                        if (info != null) {
                            if (info.headPose[0] >= ANGLE) {
                                headUpDown = false;
                                str = getResources().getString(R.string.detect_head_up);
                            } else if (info.headPose[0] <= -ANGLE) {
                                headUpDown = false;
                                str = getResources().getString(R.string.detect_head_down);
                            } else {
                                headUpDown = true;
                            }

                            boolean headLeftRight;
                            if (info.headPose[1] >= ANGLE) {
                                headLeftRight = false;
                                str = getResources().getString(R.string.detect_head_left);
                            } else if (info.headPose[1] <= -ANGLE) {
                                headLeftRight = false;
                                str = getResources().getString(R.string.detect_head_right);
                            } else {
                                headLeftRight = true;
                            }

                            if (distance && headUpDown && headLeftRight) {
                                mGoodDetect = true;
                            } else {
                                mGoodDetect = false;
                            }

                        }
                    }
                } else if (retCode == 1) {
                    str = getResources().getString(R.string.detect_head_up);
                } else if (retCode == 2) {
                    str = getResources().getString(R.string.detect_head_down);
                } else if (retCode == 3) {
                    str = getResources().getString(R.string.detect_head_left);
                } else if (retCode == 4) {
                    str = getResources().getString(R.string.detect_head_right);
                } else if (retCode == 5) {
                    str = getResources().getString(R.string.detect_low_light);
                } else if (retCode == 6) {
                    str = getResources().getString(R.string.detect_face_in);
                } else if (retCode == 7) {
                    str = getResources().getString(R.string.detect_face_in);
                } else if (retCode == 10) {
                    str = getResources().getString(R.string.detect_keep);
                } else if (retCode == 11) {
                    str = getResources().getString(R.string.detect_occ_right_eye);
                } else if (retCode == 12) {
                    str = getResources().getString(R.string.detect_occ_left_eye);
                } else if (retCode == 13) {
                    str = getResources().getString(R.string.detect_occ_nose);
                } else if (retCode == 14) {
                    str = getResources().getString(R.string.detect_occ_mouth);
                } else if (retCode == 15) {
                    str = getResources().getString(R.string.detect_right_contour);
                } else if (retCode == 16) {
                    str = getResources().getString(R.string.detect_left_contour);
                } else if (retCode == 17) {
                    str = getResources().getString(R.string.detect_chin_contour);
                }

                boolean faceChanged = true;
                if (infos != null && infos[0] != null) {
                    if (infos[0].face_id == mCurFaceId) {
                        faceChanged = false;
                    } else {
                        faceChanged = true;
                    }
                    mCurFaceId = infos[0].face_id;
                }

                if (faceChanged) {
                    showProgressBar(false);
                }

                final int resultCode = retCode;
                if (!(mGoodDetect && retCode == 0)) {
                    if (faceChanged) {
                        showProgressBar(false);
                    }
                }

                if (retCode == 6 || retCode == 7 || retCode < 0) {
                    rectView.processDrawState(true);
                } else {
                    rectView.processDrawState(false);
                }

                mCurTips = str;
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if ((System.currentTimeMillis() - mLastTipsTime) > 1000) {
//                            nameTextView.setText(mCurTips);
//                            mLastTipsTime = System.currentTimeMillis();
//                        }
//                        if (mGoodDetect && resultCode == 0) {
//                            nameTextView.setText("");
//                            showProgressBar(true);
//                        }
//                    }
//                });

                if (infos == null) {
                    mGoodDetect = false;
                }


            }
        });
        faceDetectManager.setOnTrackListener(new FaceFilter.OnTrackListener() {
            @Override
            public void onTrack(FaceFilter.TrackedModel trackedModel) {
                if (trackedModel.meetCriteria() && mGoodDetect) {
                    // upload(trackedModel);
                    mGoodDetect = false;
                    if (!mSavedBmp && mBeginDetect) {
                        if (saveFaceBmp(trackedModel)) {
                            String filePath = ImageSaveUtil.loadCameraBitmapPath(getContext(), "head_tmp.jpg");
                             File file = new File(filePath);
                            if (!file.exists()) {
                                SysUtils.log("人脸文件不存在");
                                if (FaceUtils.scanFaceListener != null) {
                                    FaceUtils.scanFaceListener.onFaild(-1, "人脸文件不存在");
                                }
                                return;
                            }
                            registerFace(file);

                        }
                    }
                }
            }
        });


        rectView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startFace();
                rectView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        ICameraControl control = cameraImageSource.getCameraControl();
        control.setPreviewView(previewView);
        // 设置检测裁剪处理器
        faceDetectManager.addPreProcessor(cropProcessor);

        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);

        if (isPortrait) {
            previewView.setScaleType(PreviewView.ScaleType.FIT_WIDTH);
        } else {
            previewView.setScaleType(PreviewView.ScaleType.FIT_HEIGHT);
        }
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        cameraImageSource.getCameraControl().setDisplayOrientation(rotation);
        //   previewView.getTextureView().setScaleX(-1);
        nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mSuccessView = (ImageView) view.findViewById(R.id.success_image);
        mSuccessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mSuccessView.getTag() == null) {
                    Rect rect = rectView.getFaceRoundRect();
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSuccessView.getLayoutParams();
                    int w = (int) getResources().getDimension(R.dimen.success_width);
                    rlp.setMargins(
                            rect.centerX() - (w / 2),
                            rect.top - (w / 2),
                            0,
                            0);
                    mSuccessView.setLayoutParams(rlp);
                    mSuccessView.setTag("setlayout");
                }
                mSuccessView.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mSuccessView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mSuccessView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    /**
     * 注册人脸用户
     *image 图片信息(总数据大小应小于10M)，图片上传方式根据image_type来判断
     *
     * image_type 图片类型 BASE64:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；URL:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)；FACE_TOKEN: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     *
     * group_id 用户组id（由数字、字母、下划线组成
     *
     * user_id 用户id（由数字、字母、下划线组成）
     *
     *可选： user_info 用户资料
     *
     *可选： quality_control 图片质量控制 NONE: 不进行控制 LOW:较低的质量要求 NORMAL: 一般的质量要求 HIGH: 较高的质量要求 默认 NONE
     *
     * 可选：liveness_control 活体检测控制 NONE: 不进行控制 LOW:较低的活体要求(高通过率 低攻击拒绝率) NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH: 较高的活体要求(高攻击拒绝率 低通过率) 默认NONE
     */

    private void registerFace(File filePath) {


        String base64Img = "";
        try {
            byte[] buf = readFile(filePath);

            base64Img = new String(Base64.encode(buf, Base64.NO_WRAP));

        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("image",base64Img);
        hashMap.put("image_type","BASE64");
        hashMap.put("group_id",registerFaceParams.group);
        hashMap.put("user_id",registerFaceParams.uuid);
        hashMap.put("user_info",registerFaceParams.name);
        hashMap.put("quality_control",ApiConfig.getQualityControl());
        hashMap.put("liveness_control",ApiConfig.getLivenessControl());

        Call<ResultInfo> call = ApiConfig.getInstants().create(ApiService.class).register(hashMap);
        API.getBaiduObject(call, AccessToken.class, new ApiCallBack<RegistFaceResult>() {
            @Override
            public void onSuccess(int msgCode, String msg, RegistFaceResult accessToken) {

                SysUtils.log("dyc","======注册成功");

            }
            @Override
            public void onFaild(int msgCode, String msg) {
                SysUtils.log("dyc","======注册成功");

            }
        });
    }


    private void init() {
        FaceSDKManager.getInstance().getFaceTracker(getActivity()).set_min_face_size(200);
        FaceSDKManager.getInstance().getFaceTracker(getActivity()).set_isCheckQuality(true);
        // 该角度为商学，左右，偏头的角度的阀值，大于将无法检测出人脸，为了在1：n的时候分数高，注册尽量使用比较正的人脸，可自行条件角度
        FaceSDKManager.getInstance().getFaceTracker(getActivity()).set_eulur_angle_thr(15, 15, 15);
        FaceSDKManager.getInstance().getFaceTracker(getActivity()).set_isVerifyLive(true);
        FaceSDKManager.getInstance().getFaceTracker(getActivity()).set_notFace_thr(0.2f);
        FaceSDKManager.getInstance().getFaceTracker(getActivity()).set_occlu_thr(0.1f);
        initBrightness();
    }


    private void initBrightness() {
        int brightness = BrightnessTools.getScreenBrightness(getActivity());
        if (brightness < 200) {
            BrightnessTools.setBrightness(getActivity(), 200);
        }
    }

    private void initScreen() {
        WindowManager manager = getActivity().getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;
    }


    private void startFace() {

        Rect dRect = rectView.getFaceRoundRect();

//        //   RectF newDetectedRect = new RectF(detectedRect);
        int preGap = 10;
        int w = 2;
//
        int orientation = getResources().getConfiguration().orientation;
        boolean isPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT);
        if (isPortrait) {
            // 检测区域矩形宽度
            int rWidth = mScreenW - 2 * preGap;
            // 圆框宽度
            int dRectW = dRect.width();
            // 检测矩形和圆框偏移
            int h = (rWidth - dRectW) / 2;
            //  Log.d("liujinhui hi is:", " h is:" + h + "d is:" + (dRect.left - 150));
            int rLeft = w;
            int rRight = rWidth - w;
            int rTop = dRect.top - h - preGap + w;
            int rBottom = rTop + rWidth - w;

            //  Log.d("liujinhui", " rLeft is:" + rLeft + "rRight is:" + rRight + "rTop is:" + rTop + "rBottom is:" + rBottom);
            RectF newDetectedRect = new RectF(rLeft, rTop, rRight, rBottom);
            cropProcessor.setDetectedRect(newDetectedRect);
        } else {
            int rLeft = mScreenW / 2 - mScreenH / 2;
            int rRight = mScreenW / 2 + mScreenH / 2;
            int rTop = 0;
            int rBottom = mScreenH;

            RectF newDetectedRect = new RectF(rLeft, rTop, rRight, rBottom);
            cropProcessor.setDetectedRect(newDetectedRect);
        }
        faceDetectManager.start();

        initWaveview(dRect);
    }

    private void initWaveview(Rect rect) {


        RelativeLayout.LayoutParams waveParams = new RelativeLayout.LayoutParams(
                rect.width(), rect.height());

        waveParams.setMargins(rect.left, rect.top, rect.left, rect.top);
        waveParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        waveParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mWaveview = new WaveView(getActivity());
        rootView.addView(mWaveview, waveParams);

        mWaveHelper = new WaveHelper(mWaveview);
        mWaveview.setShapeType(WaveView.ShapeType.CIRCLE);
        mWaveview.setWaveColor(
                Color.parseColor("#28FFFFFF"),
                Color.parseColor("#3cFFFFFF"));

//        mWaveview.setWaveColor(
//                Color.parseColor("#28f16d7a"),
//                Color.parseColor("#3cf16d7a"));

        int mBorderColor = Color.parseColor("#28f16d7a");
        mWaveview.setBorder(0, mBorderColor);
        mWaveHelper.start();
    }


    @Override
    public void onStop() {
        super.onStop();
        faceDetectManager.stop();
        mDetectStoped = true;
        mHandler.removeMessages(MSG_INITVIEW);
        mHandler.removeMessages(MSG_BEGIN_DETECT);
        if (mWaveview != null) {
            mWaveview.setVisibility(View.GONE);
            mWaveHelper.cancel();
        }
    }

    private void showProgressBar(final boolean show) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    if (mWaveview != null) {
                        mWaveview.setVisibility(View.VISIBLE);
                        mWaveHelper.start();
                    }
                } else {
                    if (mWaveview != null) {
                        mWaveview.setVisibility(View.GONE);
                        mWaveHelper.cancel();
                    }
                }

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWaveview != null) {
            mWaveHelper.cancel();
            mWaveview.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDetectStoped) {
            faceDetectManager.start();
            mDetectStoped = false;
        }

    }


    private class InnerHandler extends Handler {
        private WeakReference<Activity> mWeakReference;

        public InnerHandler(Activity activity) {
            super();
            this.mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }
            Activity activity = mWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg == null) {
                return;

            }
            switch (msg.what) {
                case MSG_INITVIEW:
                    visibleView();
                    break;
                case MSG_BEGIN_DETECT:
                    mBeginDetect = true;
                    break;
                default:
                    break;
            }
        }
    }

    private void visibleView() {
        mInitView.setVisibility(View.INVISIBLE);
    }

    private boolean saveFaceBmp(FaceFilter.TrackedModel model) {

        final Bitmap face = model.cropFace();
        if (face != null) {
            ImageSaveUtil.saveCameraBitmap(getActivity(), face, "head_tmp.jpg");
        }
        String filePath = ImageSaveUtil.loadCameraBitmapPath(getActivity(), "head_tmp.jpg");
        final File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        boolean saved = false;
        try {
            byte[] buf = readFile(file);
            if (buf.length > 0) {
                saved = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!saved) {
            Log.d("fileSize", "file size >=-99");
        } else {
            mSavedBmp = true;
        }
        return saved;
    }


    public static byte[] readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

}
