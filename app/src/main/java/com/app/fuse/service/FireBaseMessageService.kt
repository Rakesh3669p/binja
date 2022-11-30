package com.app.fuse.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.fuse.R
import com.app.fuse.utils.Constants.Companion.REQUEST_CODE_NOTIFICATION
import com.app.fuse.ui.chatmodule.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "Service"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Notification Message Body: ${remoteMessage.data}")
        sendNotification(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            try {

                val jsonObject = JSONObject(remoteMessage.data.toString())
                handleNotification(remoteMessage, jsonObject)
            } catch (e: Exception) {
                Log.d(TAG, "Notification_Exception.. ${e.message}")
            }
        }

    }

    private fun handleNotification(remoteMessage: RemoteMessage, jsonObject: JSONObject) {
        val data: JSONObject = jsonObject.getJSONObject("data")
        val type: String = jsonObject.getString("type")

        if (type == "message") {
            val userID = data.getString("to_user_id")
            val userName = data.getString("username")
            val profile = data.getString("profile")
            val bio = data.getString("designation")
            val intent = Intent(this@MyFirebaseMessagingService, ChatActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("friendID", userID)
                putExtra("friendUserName", userName)
                if (profile != null) {
                    putExtra("profile", profile)
                } else {
                    putExtra("profile", "")
                }

                putExtra("friendBio", bio)
            }

            val pendingIntent = PendingIntent.getActivity(
                this@MyFirebaseMessagingService,
                REQUEST_CODE_NOTIFICATION,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            sendNotification(remoteMessage)

        }
    }


    private fun sendNotification(remoteMessage: RemoteMessage) {
        val NOTIFICATION_ID = 2
        val NOTIFICATION_CHANNEL_ID:String = R.string.default_notification_channel_id.toString()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Binja", NotificationManager.IMPORTANCE_HIGH)
            // Configure the notification channel.
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentText(remoteMessage.notification!!.body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.background)
            .setSound(defaultSoundUri)
            /*.setContentIntent(pendingIntent)*/
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    }
}