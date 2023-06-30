package mrigapps.android.test_project
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseInstanceIDService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body

            createNotificationChannel()
            showNotification(title, body)

            Log.d("check", "Message received from: ${remoteMessage.from}")
        }

    }

    private fun createNotificationChannel() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "YourChannelId"
            val channelName = "YourChannelName"
            val channelDescription = "YourChannelDescription"

            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDescription
            channel.enableLights(true)
            channel.lightColor = Color.GREEN

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {

        Log.d("token1", "New token: $token")

    }

    private fun showNotification(title: String?, body: String?) {
        val notificationBuilder = NotificationCompat.Builder(this, "YourChannelId")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.chat)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseInstanceID"
    }
}