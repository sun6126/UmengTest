package com.example.youmengtest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * 应用角标工具类
 */
object AppShortCutUtil {
    private var notificationId = 0
    fun setCount(count: Int, context: Context?): Boolean {
        return if (count >= 0 && context != null) {
            Log.d("BRAND", Build.BRAND)
            when (Build.BRAND.toLowerCase()) {
                "xiaomi" -> {
                    Handler().postDelayed(Runnable { setNotificationBadge(count, context) }, 3000)
                    true
                }
                "huawei" -> setHuaweiBadge(count, context)
                "honor" -> setHonorBadge(count, context)
                "samsung" -> setSamsungBadge(count, context)
                "oppo" -> setOPPOBadge(count, context) || setOPPOBadge2(count, context)
                "vivo" -> setVivoBadge(count, context)
                "lenovo" -> setZukBadge(count, context)
                "htc" -> setHTCBadge(count, context)
                "sony" -> setSonyBadge(count, context)
                else -> setNotificationBadge(count, context)
            }
        } else {
            false
        }
    }

    fun setNotificationBadge(count: Int, context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 8.0之后添加角标需要NotificationChannel
            val channel = NotificationChannel(
                "badge", "badge",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notification: Notification = NotificationCompat.Builder(context, "badge")
            .setContentTitle(context.getResources().getString(R.string.application_angle))
            .setContentText(
                context.getResources().getString(R.string.you_have) + count + context.getResources()
                    .getString(R.string.unread_message)
            )
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.getResources(),
                    R.mipmap.ic_launcher
                )
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setChannelId("badge")
            .setNumber(count)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL).build()
        // 小米
        if (Build.BRAND.equals("xiaomi", ignoreCase = true)) {
            setXiaomiBadge(count, notification)
        }
        notificationManager.notify(notificationId++, notification)
        return true
    }

    private fun setXiaomiBadge(count: Int, notification: Notification) {
        try {
            val field: Field = notification.javaClass.getDeclaredField("extraNotification")
            val extraNotification: Any = field.get(notification)
            val method: Method = extraNotification.javaClass.getDeclaredMethod(
                "setMessageCount",
                Int::class.javaPrimitiveType
            )
            method.invoke(extraNotification, count)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHonorBadge(count: Int, context: Context): Boolean {
        return try {
            val launchClassName = getLauncherClassName(context)
            if (TextUtils.isEmpty(launchClassName)) {
                return false
            }
            val bundle = Bundle()
            bundle.putString("package", context.getPackageName())
            bundle.putString("class", launchClassName)
            bundle.putInt("badgenumber", count)
            context.getContentResolver().call(
                Uri.parse(
                    "content://com.huawei.android.launcher" +
                            ".settings/badge/"
                ), "change_badge", null, bundle
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setHuaweiBadge(count: Int, context: Context): Boolean {
        return try {
            val launchClassName = getLauncherClassName(context)
            if (TextUtils.isEmpty(launchClassName)) {
                return false
            }
            val bundle = Bundle()
            bundle.putString("package", context.getPackageName())
            bundle.putString("class", launchClassName)
            bundle.putInt("badgenumber", count)
            context.getContentResolver().call(
                Uri.parse("content://com.huawei.android.launcher" + ".settings/badge/"),
                "change_badge",
                null,
                bundle
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setSamsungBadge(count: Int, context: Context): Boolean {
        return try {
            val launcherClassName = getLauncherClassName(context)
            if (TextUtils.isEmpty(launcherClassName)) {
                return false
            }
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
            intent.putExtra("badge_count", count)
            intent.putExtra("badge_count_package_name", context.getPackageName())
            intent.putExtra("badge_count_class_name", launcherClassName)
            context.sendBroadcast(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Deprecated("")
    private fun setOPPOBadge(count: Int, context: Context): Boolean {
        return try {
            val extras = Bundle()
            extras.putInt("app_badge_count", count)
            context.getContentResolver().call(
                Uri.parse("content://com.android.badge/badge"),
                "setAppBadgeCount", count.toString(), extras
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Deprecated("")
    private fun setOPPOBadge2(count: Int, context: Context): Boolean {
        return try {
            val intent = Intent("com.oppo.unsettledevent")
            intent.putExtra("packageName", context.getPackageName())
            intent.putExtra("number", count)
            intent.putExtra("upgradeNumber", count)
            val packageManager: PackageManager = context.getPackageManager()
            val receivers = packageManager.queryBroadcastReceivers(intent, 0)
            if (receivers != null && receivers.size > 0) {
                context.sendBroadcast(intent)
            } else {
                val extras = Bundle()
                extras.putInt("app_badge_count", count)
                context.getContentResolver().call(
                    Uri.parse("content://com.android.badge/badge"),
                    "setAppBadgeCount", null, extras
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Deprecated("")
    private fun setVivoBadge(count: Int, context: Context): Boolean {
        return try {
            val launcherClassName = getLauncherClassName(context)
            if (TextUtils.isEmpty(launcherClassName)) {
                return false
            }
            val intent = Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM")
            intent.putExtra("packageName", context.getPackageName())
            intent.putExtra("className", launcherClassName)
            intent.putExtra("notificationNum", count)
            context.sendBroadcast(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setZukBadge(count: Int, context: Context): Boolean {
        return try {
            val extra = Bundle()
            val ids: ArrayList<String> = ArrayList()
            // 以列表形式传递快捷方式id，可以添加多个快捷方式id
            //        ids.add("custom_id_1");
            //        ids.add("custom_id_2");
            extra.putStringArrayList("app_shortcut_custom_id", ids)
            extra.putInt("app_badge_count", count)
            val contentUri: Uri = Uri.parse("content://com.android.badge/badge")
            val bundle: Bundle? = context.contentResolver.call(
                contentUri, "setAppBadgeCount", null,
                extra
            )
            bundle != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setHTCBadge(count: Int, context: Context): Boolean {
        return try {
            val launcherComponentName = getLauncherComponentName(context)
                ?: return false
            val intent1 = Intent("com.htc.launcher.action.SET_NOTIFICATION")
            intent1.putExtra(
                "com.htc.launcher.extra.COMPONENT", launcherComponentName
                    .flattenToShortString()
            )
            intent1.putExtra("com.htc.launcher.extra.COUNT", count)
            context.sendBroadcast(intent1)
            val intent2 = Intent("com.htc.launcher.action.UPDATE_SHORTCUT")
            intent2.putExtra("packagename", launcherComponentName.packageName)
            intent2.putExtra("count", count)
            context.sendBroadcast(intent2)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setSonyBadge(count: Int, context: Context): Boolean {
        val launcherClassName = getLauncherClassName(context)
        return if (TextUtils.isEmpty(launcherClassName)) {
            false
        } else try {
            //官方给出方法
            val contentValues = ContentValues()
            contentValues.put("badge_count", count)
            contentValues.put("package_name", context.getPackageName())
            contentValues.put("activity_name", launcherClassName)
            val asyncQueryHandler = SonyAsyncQueryHandler(
                context
                    .getContentResolver()
            )
            asyncQueryHandler.startInsert(
                0, null, Uri.parse(
                    "content://com.sonymobile.home" +
                            ".resourceprovider/badge"
                ), contentValues
            )
            true
        } catch (e: Exception) {
            try {
                //网上大部分使用方法
                val intent = Intent("com.sonyericsson.home.action.UPDATE_BADGE")
                intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", count > 0)
                intent.putExtra(
                    "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",
                    launcherClassName
                )
                intent.putExtra(
                    "com.sonyericsson.home.intent.extra.badge.MESSAGE",
                    count.toString()
                )
                intent.putExtra(
                    "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context
                        .getPackageName()
                )
                context.sendBroadcast(intent)
                true
            } catch (e1: Exception) {
                e1.printStackTrace()
                false
            }
        }
    }

    private fun getLauncherClassName(context: Context): String {
        val launchComponent = getLauncherComponentName(context)
        return launchComponent?.className ?: ""
    }

    private fun getLauncherComponentName(context: Context): ComponentName? {
        val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(
            context.getPackageName()
        )
        return launchIntent?.component
    }

    internal class SonyAsyncQueryHandler(cr: ContentResolver?) :
        AsyncQueryHandler(cr)
}