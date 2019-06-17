package net.zgxyzx.aio.classcard.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.aip.utils.SysUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;

import net.zgxyzx.aio.classcard.R;

public class MainActivity extends AppCompatActivity {


    private int REQUEST_CAMERO_CODE = 0x110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if (SysUtils.isCheckPemission()) {
            checkCamerPermission();
        }

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RegisterFaceActivity.class));

        }
    });

    findViewById(R.id.btnVertify).

    setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
                startActivity(new Intent(MainActivity.this, DetectLoginActivity.class));
        }
    });


       SysUtils.log(ScreenUtils.getScreenWidth()+"---"+ ScreenUtils.getScreenHeight());
}


    //检查摄像头权限
    private void checkCamerPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERO_CODE);
        }
    }


    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERO_CODE) {
            if (grantResults[0] == 0) {
            } else {
                ToastUtils.showShort("请确认拍照权限");
            }
        }
    }
}
