package com.example.sms_cqa

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.*
import java.text.SimpleDateFormat
import java.util.*


//private const val TAG = "MyBroadcastReceiver"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //        判断并获取权限
        var smsPermissions = arrayOf("Manifest.permission.RECEIVE_SMS")
        var isSmsGranted =
            android.content.pm.PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            )
        if (isSmsGranted) {
            ActivityCompat.requestPermissions(this, smsPermissions, 1)
        }

        smsPermissions = arrayOf("Manifest.permission.READ_SMS")
        isSmsGranted =
            android.content.pm.PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            )
        if (isSmsGranted) {
            ActivityCompat.requestPermissions(this, smsPermissions, 1)
        }
        //        注册广播接收器
        val br: BroadcastReceiver = MyBroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction("android.provider.Telephony.SMS_RECEIVED")
            priority = 1000
        }
        registerReceiver(br, filter)
        }
    }
    class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
//                    webSocket.send("{\"action\":\"send_private_msg\",\"params\":{\"user_id\":1622956095,\"message\":\"你好你好\"}}");
//        发送
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
//        接收
            //  logger.e("onMessage","onMessage: " + text);
        }
        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//            远程端暗示没有数据交互时回调
//            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            //服务器关闭后
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            //服务器中断
            Log.e("onFailure","onFailure: " + t.toString()+response.toString())
        }
    }

    private class MyBroadcastReceiver : BroadcastReceiver() {
        val listener = EchoWebSocketListener()
        val request: Request = Request.Builder()
            .url("ws://101.200.44.174:6700/api")
            .addHeader("Authorization","Bearer gSGIgfdsosGIYgfds").build()
        val client = OkHttpClient()
        val websocket = client.newWebSocket(request, listener)

        override fun onReceive(context: Context, intent: Intent) {

            val objs = intent.extras!!["pdus"] as Array<Any>?
            for (obj in objs!!) {
                val pdu = obj as ByteArray
                val sms: SmsMessage = SmsMessage.createFromPdu(pdu)
                // 短信的内容
                val message1 = "时间:" + SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sms.getTimestampMillis())
                val message2 ="发件人：" + sms.getOriginatingAddress()
                val message3 = sms.getMessageBody()
                val json = "{\"action\":\"send_private_msg\",\"params\":{\"user_id\":1622956095,\"message\":[{\"type\":\"text\",\"data\":{\"text\":\"${message1}\\n\"}},{\"type\":\"text\",\"data\":{\"text\":\"${message2}\\n\"}},{\"type\":\"text\",\"data\":{\"text\":\"${message3}\\n\"}}]}}"
                websocket.send(json)
            }
        }
    }

