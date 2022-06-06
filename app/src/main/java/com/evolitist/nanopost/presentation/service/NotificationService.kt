package com.evolitist.nanopost.presentation.service

import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.evolitist.nanopost.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    companion object {
        private const val POSTS_CHANNEL_ID = "posts"
        private var postNotificationId = 1
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onNewToken(token: String) {
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            val notification = NotificationCompat.Builder(this, POSTS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(it.title)
                .setContentText(it.body)
                .build()
            NotificationManagerCompat.from(this)
                .notify(postNotificationId++, notification)
        }
    }

    private fun createChannel() {
        val channel = NotificationChannelCompat.Builder(POSTS_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName("New posts")
            .build()
        NotificationManagerCompat.from(applicationContext)
            .createNotificationChannel(channel)
    }
}
