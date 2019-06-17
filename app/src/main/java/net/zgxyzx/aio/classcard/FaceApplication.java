package net.zgxyzx.aio.classcard;

import android.app.Application;

import com.baidu.aip.FaceUtils;
import com.baidu.aip.api.ApiConfig;
import com.baidu.aip.api.ApiTest;

public class FaceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FaceUtils.init(this, Config.groupID, Config.licenseID, Config.licenseFileName);
        ApiConfig.init(this, "https://aip.baidubce.com");
        ApiConfig.setLogFilter("dyc");
        ApiConfig.setDebug(true);
        ApiTest.getToken(Config.apiKey, Config.secretKey);
    }
}
