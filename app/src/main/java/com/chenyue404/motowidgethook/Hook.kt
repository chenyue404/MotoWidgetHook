package com.chenyue404.motowidgethook

import android.content.Context
import android.content.Intent
import android.view.View
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XSharedPreferences
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

        if (packageName != "com.motorola.timeweatherwidget") return

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
}