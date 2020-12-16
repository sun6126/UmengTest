package com.example.youmengtest

import android.app.Application
import android.app.Notification
import android.content.Context
import android.util.Log
import android.widget.Toast
import anet.channel.util.Utils.context
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback

import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage


class MyApplication : Application() {

    val TAG = "sunbaoqi"

    // 自定义通知栏打开动作
    val notificationClickHandler = object : UmengNotificationClickHandler() {
        override fun dealWithCustomAction(p0: Context?, p1: UMessage?) {
            super.dealWithCustomAction(p0, p1)
            Toast.makeText(p0, p1?.custom, Toast.LENGTH_LONG).show();
        }
    }

    // UmengMessageHandler类负责处理消息，包括通知和自定义消息。其中，成员函数getNotification负责定义通知栏样式。
    val messageHandler = object : UmengMessageHandler() {

        // 成员函数getNotification负责定义通知栏样式
        override fun getNotification(p0: Context?, p1: UMessage?): Notification {
            return super.getNotification(p0, p1)
        }
    }

    override fun onCreate() {
        super.onCreate()
// 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
// 参数一：当前上下文context；
// 参数二：应用申请的Appkey（需替换）；
// 参数三：渠道名称；
// 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
// 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        UMConfigure.init(
            this,
            "5faa54e71c520d3073a4f775",
            "Umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            "b8550efc3e820dc9ae1dafbc67367ff6"
        );

        //获取消息推送代理示例
        //获取消息推送代理示例
        val mPushAgent = PushAgent.getInstance(this)
        //注册推送服务，每次调用register方法都会回调该接口
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {
            override fun onSuccess(deviceToken: String) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG, "注册成功：deviceToken：-------->  $deviceToken")
            }

            override fun onFailure(s: String, s1: String) {
                Log.e(TAG, "注册失败：-------->  s:$s,s1:$s1")
            }
        })

        mPushAgent.notificationClickHandler = notificationClickHandler
    }


}