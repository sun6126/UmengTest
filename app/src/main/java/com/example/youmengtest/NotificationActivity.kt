package com.example.youmengtest

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        createNotificationChannel()
    }

    fun createNotificationChannel() {
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

    fun sendChatMsg(view: View) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(this, "chat")
            .setContentTitle("收到一条聊天消息")
            .setContentText("今天中午吃什么？")
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
        manager.notify(1, notification)
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


}