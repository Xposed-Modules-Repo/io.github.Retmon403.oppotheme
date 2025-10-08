package io.github.Retmon403.oppotheme;

//以下代码由deepseek辅助编写。。。
//android不是我的强项，屎山将就用。

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
        log("set_vip_status_hook");
        // VIP 状态获取方法
        set_method_hook(lpparam,
                "com.oppo.cdo.card.theme.dto.vip.VipUserDto",
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
        set_method_hook(lpparam,
                "com.oppo.cdo.card.theme.dto.vip.VipUserDto",
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
        set_method_hook(lpparam,
                "com.oppo.cdo.card.theme.dto.page.WeatherPageResponseDto",
                "getVipStatus",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        XposedHelpers.setIntField(param.thisObject, "vipStatus", 1);
                    }
                }
        );

// 是否 VIP
        set_method_hook(lpparam,
                "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto",
                "getIsVip",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        param.setResult(new Integer(1));
                    }
                }
        );

// VIP 可用状态
        set_method_hook(lpparam,
                "com.oppo.cdo.theme.domain.dto.response.ResourceItemDto",
                "getIsVipAvailable",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        param.setResult(new Integer(1));
                    }
                }
        );

    }

    public void set_ad_hook(XC_LoadPackage.LoadPackageParam lpparam) {
        log("set_ad_hook");
        set_method_hook(lpparam, "com.oppo.cdo.card.theme.dto.SplashDto", "getAdData", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam pparam) {
                XposedHelpers.callMethod(pparam.thisObject, "setShowTime", 1);
                XposedHelpers.callMethod(pparam.thisObject, "setIsSkip", true);
            }
        });

    }


    public void set_method_hook(XC_LoadPackage.LoadPackageParam lpparam, String cls, String method, XC_MethodHook callback, Class<?>... parameterTypes) {
        Class<?> targetClass = XposedHelpers.findClassIfExists(cls, lpparam.classLoader);
        if (targetClass != null) {
            try {
                // 检查方法是否存在
                Method targetMethod = targetClass.getDeclaredMethod(method, parameterTypes);
                if (targetMethod != null) {
                    XposedBridge.hookMethod(targetMethod, callback);
                    log("✅ hook: " + cls + "." + method);
                } else {
                    log("❌ not found: " + cls + "." + method);
                }
            } catch (NoSuchMethodException e) {
                log("❌ not found: " + cls + "." + method + " - " + e.getMessage());
            } catch (Exception e) {
                log("❌ error: " + cls + "." + method + " - " + e.getMessage());
            }
        } else {
            log("❌ not found class: " + cls);
        }
    }

    public void set_resource_trial_hook(XC_LoadPackage.LoadPackageParam lpparam) {
        log("set_resource_trial_hook");
        // 试用定时器回调函数 禁止调用
        set_method_hook(lpparam,
                "com.nearme.themespace.trial.ThemeTrialExpireReceiver",
                "onReceive",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        log("拦截ThemeTrialExpireReceiver.onReceive方法");
                        param.setResult(null);
                    }
                },
                Context.class, Intent.class
        );

        set_method_hook(lpparam,
                "com.oplus.aiunit.vision.p53",
                "b",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        log("hook p53.b方法");
                        param.setResult(null);
                    }
                },
                Context.class
        );

        // 试用到期广播接收器
        set_method_hook(lpparam,
                "com.nearme.themespace.trial.ThemeTrialExpireReceiver",
                "a",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        //返回false 禁用广播
                        log("ThemeTrialExpireReceiver");
                        param.setResult(false);
                    }
                },
                Context.class, String.class
        );


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
    
