package com.example.youmengtest

import android.annotation.TargetApi
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.multidex.MultiDex
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.mezu.MeizuRegister
import org.android.agoo.xiaomi.MiPushRegistar


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
        override fun getNotification(context: Context?, msg: UMessage?): Notification {
            return createNotification(context!!, msg!!)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 创建通知渠道
        initNotificationChannel()

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
        // mPushAgent.setNotificaitonOnForeground(false) // 应用在前台时不显示通知，要在register之前调用


        mPushAgent.notificationClickHandler = notificationClickHandler
        mPushAgent.messageHandler = messageHandler

        HuaWeiRegister.register(this)
        MeizuRegister.register(this, "137761", "9ed2d067b4254d88967e23004bec229f")
        MiPushRegistar.register(this, "2882303761518932678", "5991893249678")

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
    }

    var notificationNum = 0

    fun createNotification(context: Context, msg: UMessage): Notification {
        return NotificationCompat.Builder(context, "chat")
            .setContentTitle(msg.title)
            .setContentText(msg.text)
            .setWhen(System.currentTimeMillis())
            .setNumber(++notificationNum) // 设置app角标数量
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setAutoCancel(true)
            .build()
    }

    fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 安卓八版本以上，要创建通知渠道
            var channelId = "chat"
            var channelName = "聊天消息"
            var importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)

            channelId = "subscribe"
            channelName = "订阅消息"
            importance = NotificationManager.IMPORTANCE_DEFAULT
            createNotificationChannel(channelId, channelName, importance)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}