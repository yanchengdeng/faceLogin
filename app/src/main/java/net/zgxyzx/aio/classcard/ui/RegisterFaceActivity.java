package net.zgxyzx.aio.classcard.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.aip.FaceUtils;
import com.baidu.aip.bean.FUNCTION_TYPE;
import com.baidu.aip.bean.RegistFaceResult;
import com.baidu.aip.bean.RegisterFaceParams;
import com.baidu.aip.bean.VertifyFaceResult;
import com.baidu.aip.listeners.ScanFaceListener;
import com.baidu.aip.listeners.VertifyFaceListener;
import com.baidu.aip.ui.RegisterOrVertifyFaceFragment;
import com.baidu.aip.utils.SysUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;

import net.zgxyzx.aio.classcard.Config;
import net.zgxyzx.aio.classcard.R;

//模拟注册
public class RegisterFaceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);
        RegisterFaceParams registerFaceParams = new RegisterFaceParams(Config.groupID,  "1306738083602024258","邓言诚");
        FragmentUtils.add(getSupportFragmentManager(),
                RegisterOrVertifyFaceFragment.newInstance(FUNCTION_TYPE.FUNCTION_TYPE_REGISTER, registerFaceParams)
                , R.id.fl_main);

        FaceUtils.setScanFaceListener(new ScanFaceListener() {
            @Override
            public void onSucess(RegistFaceResult result) {
                SysUtils.log(result.getFaceToken());
                SysUtils.log(result.getNativeHeadPhoto());

                ToastUtils.showShort("注册成功");

            }

            @Override
            public void onFaild(int code, String message) {
                SysUtils.log(code+"--"+message);
            }
        });


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
