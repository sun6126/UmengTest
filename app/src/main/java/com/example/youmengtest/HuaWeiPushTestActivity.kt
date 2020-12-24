package com.example.youmengtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.umeng.message.UmengNotifyClickActivity
import org.android.agoo.common.AgooConstants


class HuaWeiPushTestActivity : UmengNotifyClickActivity() {

    val TAG = "sunbaoqi"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hua_wei_push_test)
    }

    override fun onMessage(intent: Intent) {
        super.onMessage(intent) //此方法必须调用，否则无法统计打开数
        val body = intent.getStringExtra(AgooConstants.MESSAGE_BODY)
        Log.i(TAG, "这是华为通道过来的消息")
        Log.i(TAG, body.toString())
    }
}