package com.github.tvbox.osc.bbox.base;

import android.app.Activity;
import androidx.multidex.MultiDexApplication;
import com.github.tvbox.osc.bbox.bean.VodInfo;
import com.github.tvbox.osc.bbox.callback.EmptyCallback;
import com.github.tvbox.osc.bbox.callback.LoadingCallback;
import com.github.tvbox.osc.bbox.data.AppDataManager;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.github.tvbox.osc.bbox.util.*;
import com.github.tvbox.osc.bbox.util.js.JSEngine;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.hawk.Hawk;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pj567
 * @date :2020/12/17
 * @description:
 */
public class App extends MultiDexApplication {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initParams();
        // OKGo
        OkGoHelper.init(); //台标获取
        EpgUtil.init();
        // 初始化Web服务器
        ControlManager.init(this);
        //初始化数据库
        AppDataManager.init();
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .commit();
        AutoSizeConfig.getInstance().setCustomFragment(true).getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
        PlayerHelper.init();
        JSEngine.getInstance().create();
        FileUtils.cleanPlayerCache();
    }

    private void initParams() {
        // Hawk
        Hawk.init(this).build();

        String defaultApiName = "默认-自备份线路";
        String defaultApi = "http://xn--a-206cq03b.seetv.eu.org/";

        HashMap<String, String> defaultApiMap = Hawk.get(HawkConfig.API_MAP, new HashMap<>());
        defaultApiMap.put(defaultApiName, defaultApi);

        ArrayList<String> defaultApiHistory = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<>());
        defaultApiHistory.add(defaultApiName);

        Hawk.put(HawkConfig.DEBUG_OPEN, false);

        // 不添加默认线路
        // putDefault(HawkConfig.API_URL, defaultApi);
        // putDefault(HawkConfig.API_NAME, defaultApiName);
        // putDefault(HawkConfig.API_NAME_HISTORY, defaultApiHistory);
        // putDefault(HawkConfig.API_MAP, defaultApiMap);

        putDefault(HawkConfig.PLAY_TYPE, 1);
        putDefault(HawkConfig.HOME_REC, 1);
        putDefault(HawkConfig.IJK_CODEC, "硬解码");
        putDefault(HawkConfig.HOME_REC_STYLE, false);//首页多行
    }

    private void putDefault(String key, Object value) {
        if (!Hawk.contains(key)) {
            Hawk.put(key, value);
        }
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JSEngine.getInstance().destroy();
    }


    private VodInfo vodInfo;

    public void setVodInfo(VodInfo vodinfo) {
        this.vodInfo = vodinfo;
    }

    public VodInfo getVodInfo() {
        return this.vodInfo;
    }

    public Activity getCurrentActivity() {
        return AppManager.getInstance().currentActivity();
    }
}
