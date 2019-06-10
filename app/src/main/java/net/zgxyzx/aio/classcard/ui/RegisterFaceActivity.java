package net.zgxyzx.aio.classcard.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.aip.bean.FUNCTION_TYPE;
import com.baidu.aip.bean.RegisterFaceParams;
import com.baidu.aip.ui.RegisterOrVertifyFaceFragment;
import com.blankj.utilcode.util.FragmentUtils;

import net.zgxyzx.aio.classcard.Config;
import net.zgxyzx.aio.classcard.R;

//模拟注册
public class RegisterFaceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);
        RegisterFaceParams RegisterFaceParams = new RegisterFaceParams(Config.groupID,  "1306738083602024258","邓言诚");
        FragmentUtils.add(getSupportFragmentManager(),
                RegisterOrVertifyFaceFragment.newInstance(FUNCTION_TYPE.FUNCTION_TYPE_REGISTER, RegisterFaceParams)
                , R.id.fl_main);





    }





}
