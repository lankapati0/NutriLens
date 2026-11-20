package com.akhilav.nutrilens.utils
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NutriLensFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New FCM token received")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Message received")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "NutriLens"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "You have a new notification"

        Log.v("FCM_TOKEN","${title},${body}")
        NotificationHelper.showNotification(context=this,title,body)
    }
}