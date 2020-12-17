package com.example.youmengtest

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class NotificationActivity : AppCompatActivity() {

    var notificationNum: Int = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        Companion.createNotificationChannel(this)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.setShowBadge(true) // 在创建通知渠道的时候，调用了NotificationChannel的setShowBadge(true)方法，表示允许这个渠道下的通知显示角标。
        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendChatMsg(view: View) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(this, "chat")
            .setContentTitle("收到一条聊天消息")
            .setContentText("今天中午吃什么？")
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
        manager.notify(1, notification)

//        showBadgeOfEMUI(this, notificationNum)
    }

    fun sendSubscribeMsg(view: View) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "subscribe")
            .setContentTitle("收到一条订阅消息")
            .setContentText("地铁沿线30万商铺抢购中！")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setAutoCancel(true)
            .build()
        manager.notify(2, notification)
    }

    // 华为的消息角标需要事先声明两个权限：INTERNET和CHANGE_BADGE
    private fun showBadgeOfEMUI(ctx: Context, count: Int) {
        try {
            val extra = Bundle() // 创建一个包裹对象
            extra.putString("package", BuildConfig.APPLICATION_ID) // 应用的包名
            extra.putString("class", BuildConfig.APPLICATION_ID + ".MainActivity") // 应用的首屏页面路径
            extra.putInt("badgenumber", count) // 应用的消息数量
            val uri: Uri = Uri.parse("content://com.huawei.android.launcher.settings/badge/")
            // 通过内容解析器调用华为内核的消息角标服务
            ctx.getContentResolver().call(uri, "change_badge", null, extra)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        public fun createNotificationChannel(notificationActivity: NotificationActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 安卓八版本以上，要创建通知渠道
                var channelId = "chat"
                var channelName = "聊天消息"
                var importance = NotificationManager.IMPORTANCE_HIGH
                notificationActivity.createNotificationChannel(channelId, channelName, importance)

                channelId = "subscribe"
                channelName = "订阅消息"
                importance = NotificationManager.IMPORTANCE_DEFAULT
                notificationActivity.createNotificationChannel(channelId, channelName, importance)
            }
        }
    }

}