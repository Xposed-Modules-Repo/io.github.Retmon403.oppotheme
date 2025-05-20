package io.github.Retmon403.oppotheme;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    public void set_vipstatus_hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // VIP 状态获取方法
            XposedHelpers.findAndHookMethod(
                    "com.oppo.cdo.card.theme.dto.vip.VipUserDto",
                    lpparam.classLoader,
                    "getVipStatus",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedHelpers.setIntField(param.thisObject, "vipStatus", 1);
                            XposedHelpers.setIntField(param.thisObject, "vipDays", 99999);
                        }
                    }
            );


            // VIP 天数获取方法
            XposedHelpers.findAndHookMethod(
                    "com.oppo.cdo.card.theme.dto.vip.VipUserDto",
                    lpparam.classLoader,
                    "getVipDays",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedHelpers.setIntField(param.thisObject, "vipStatus", 1);
                            XposedHelpers.setIntField(param.thisObject, "vipDays", 99999);
                        }
                    }
            );

            // VIP 状态获取方法
            XposedHelpers.findAndHookMethod(
                    "com.oppo.cdo.card.theme.dto.page.WeatherPageResponseDto",
                    lpparam.classLoader,
                    "getVipStatus",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedHelpers.setIntField(param.thisObject, "vipStatus", 1);
                        }
                    }
            );

            // 是否 VIP
            XposedHelpers.findAndHookMethod(
                    "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto",
                    lpparam.classLoader,
                    "getIsVip",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            param.setResult(new Integer(1));
                        }
                    }
            );


            // VIP 可用状态
            XposedHelpers.findAndHookMethod(
                    "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto",
                    lpparam.classLoader,
                    "getIsVipAvailable",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            param.setResult(new Integer(1));
                        }
                    }
            );
        } catch (Exception e) {
            log(e.getMessage());
        }
        log("set_vipstatus_hook");
    }

    public void set_ad_hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // 去除开屏广告
            XposedHelpers.findAndHookMethod(
                    "com.oppo.cdo.card.theme.dto.SplashDto",
                    lpparam.classLoader,
                    "getAdData",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedHelpers.callMethod(param.thisObject, "setShowTime", 1);
                            XposedHelpers.callMethod(param.thisObject, "setIsSkip", true);
                        }
                    }
            );
        } catch (Exception e) {
            log(e.getMessage());
        }
        log("set_ad_hook");
    }

    public void set_resource_trial_hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
//            XposedHelpers.findAndHookMethod(
//                    "com.nearme.themespace.trial.ThemeTrialExpireReceiver",
//                    lpparam.classLoader,
//                    "a",
//                    Context.class, String.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) {
//                            //返回false 禁用广播
//                            log("ThemeTrialExpireReceiver");
//                            param.setResult(false);
//                        }
//                    }
//            );

            XposedHelpers.findAndHookMethod(
                    "com.oplus.aiunit.vision.p53",
                    lpparam.classLoader,
                    "b",
                    Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            //试用恢复管理器 最终会调用到ResourceApplyTask设置主题 提前拦截禁止向下调用
                            log("p53");
                            param.setResult(null);
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(
                    "com.nearme.themespace.trial.ThemeTrialExpireReceiver",
                    lpparam.classLoader,
                    "onReceive",
                    Context.class, Intent.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            //试用定时器回调函数 禁止调用
                            log("onReceive");
                            param.setResult(null);
                        }
                    }
            );

        } catch (Exception e) {
            log(e.getMessage());
        }
        log("set_resource_trial_hook");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        // Oppo/OnePlus 主题商城
        if (!lpparam.packageName.equals("com.heytap.themestore")) {
            return;
        }

        set_vipstatus_hook(lpparam);
        set_ad_hook(lpparam);
        set_resource_trial_hook(lpparam);
        log("本模块免费开源，禁止倒卖！");
    }

    public void log(String str) {

        XposedBridge.log(String.format("[theme_unlock] %s", str));
    }
}
    
