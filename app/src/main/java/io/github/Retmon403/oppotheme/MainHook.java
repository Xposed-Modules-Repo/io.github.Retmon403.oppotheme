package io.github.Retmon403.oppotheme;

import android.app.ActivityManager;
import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static final String TARGET_PACKAGE = "com.heytap.themestore";
    private static final String PREFS_NAME = "kill_flag";
    private static final String PROCESS = "Oppo一加主题解锁";


    public void kill_oppo_theme_store(XC_LoadPackage.LoadPackageParam lpparam) {
        // Hook Application的onCreate方法以获取正确的Context
        XposedHelpers.findAndHookMethod(
                "android.app.Application",
                lpparam.classLoader,
                "onCreate",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) param.thisObject;
                        // 使用应用自身的Context获取ActivityManager
                        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        // 直接终止目标包名的后台进程
                        am.killBackgroundProcesses(TARGET_PACKAGE);
                        XposedBridge.log(String.format("[%s] Process killed successfully", PROCESS));
                    }
                }
        );
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Oppo/OnePlus 主题商城
        if (!lpparam.packageName.equals(TARGET_PACKAGE)) {
            return;
        }
        kill_oppo_theme_store(lpparam);
        XposedBridge.log(String.format("[%s] Hook...", PROCESS));

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
        XposedBridge.log(String.format("[%s] 本模块免费开源，禁止倒卖！", PROCESS));
    }
}