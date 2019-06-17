/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package net.zgxyzx.aio.classcard.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.aip.FaceUtils;
import com.baidu.aip.bean.FUNCTION_TYPE;
import com.baidu.aip.bean.RegisterFaceParams;
import com.baidu.aip.bean.VertifyFaceResult;
import com.baidu.aip.listeners.VertifyFaceListener;
import com.baidu.aip.ui.RegisterOrVertifyFaceFragment;
import com.baidu.aip.utils.SysUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;

import net.zgxyzx.aio.classcard.Config;
import net.zgxyzx.aio.classcard.R;

/**
 * 实时检测调用identify进行人脸识别，MainActivity未给出改示例的入口，开发者可以在MainActivity调用

 */
public class DetectLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);
        RegisterFaceParams registerFaceParams = new RegisterFaceParams(Config.groupID,  "1306738083602024258","邓言诚");
        FragmentUtils.add(getSupportFragmentManager(),
                RegisterOrVertifyFaceFragment.newInstance(FUNCTION_TYPE.FUNCTION_TYPE_VERTIFY, registerFaceParams)
                , R.id.fl_main);



        FaceUtils.setVertifyFaceListener(new VertifyFaceListener() {
            @Override
            public void onSucess(VertifyFaceResult result) {
                SysUtils.log(result.getFaceToken());
                SysUtils.log(result.getUserList().toString());

                ToastUtils.showShort("验证成功");
            }

            @Override
            public void onFaild(int code, String message) {

            }
        });
    }


}
