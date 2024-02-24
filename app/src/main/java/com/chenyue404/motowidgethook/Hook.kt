package com.chenyue404.motowidgethook

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.Color
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * Created by chenyue on 2024/1/1 0001.
 */
class Hook : IXposedHookLoadPackage {
    private val TAG = "MotoWidgetHook-hook-"

    private fun log(str: String) {
        XposedBridge.log("$TAG$str")
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

//        if (packageName != "com.motorola.timeweatherwidget") return

        if (packageName == "com.motorola.timeweatherwidget") {
            XposedHelpers.findAndHookMethod(
                "com.motorola.commandcenter.Utils",
                classLoader,
                "getBackgroundLaunchAllowedIntent",
                Context::class.java,
                Intent::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val intent = param.args[1] as Intent
                        log("intent=$intent")
                        if (intent.action?.isNotEmpty() == true) {
                            intent.`package` = null
                            param.args[1] = intent
                            log("Processed")
                        }
                    }
                }
            )
        }

        if (packageName == "com.android.dialer") {
            XposedHelpers.findAndHookMethod(
                "t0",
                classLoader,
                "G",
                XposedHelpers.findClass("androidx.appcompat.widget.Toolbar", classLoader),
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        log("q0.m1")
                        val f = XposedHelpers.getObjectField(param.thisObject, "f").toString()
                        log(f)
                        if (f.contains("CallLogActivity")
                            || f.contains("PrcRecordingListActivity")
                        ) {
                            param.result = null
                        }
                    }
                }
            )
        }

        if (packageName == "com.zhihu.android") {
            XposedHelpers.findAndHookMethod(
                Resources::class.java,
                "getColor",
                Int::class.java,
                Theme::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        if (param.args[0] == 0x0) {
                            param.result = Color.BLACK
                            return
                        }
                    }
                }
            )
        }
    }
}