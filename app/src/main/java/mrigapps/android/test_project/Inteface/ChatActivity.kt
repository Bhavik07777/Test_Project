package mrigapps.android.test_project.Inteface

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mrigapps.android.test_project.Adapter.MessageAdapter
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var messagerc_view: RecyclerView
    private lateinit var messageText: EditText
    private lateinit var send_message: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var senderMessagelist: ArrayList<Message>
    private lateinit var receiverMessagelist: ArrayList<Message>
    private lateinit var databaseref: DatabaseReference
    var receiver_room: String? = null
    var sender_room: String? = null
    private lateinit var firebaseMessaging: FirebaseMessaging
    private lateinit var receiveruid: String
    private var senderuid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        senderuid = FirebaseAuth.getInstance().currentUser?.uid
        val name = intent.getStringExtra("name")
        receiveruid = intent.getStringExtra("uid").toString()

        sender_room = receiveruid + senderuid
        receiver_room = senderuid + receiveruid
        firebaseMessaging = FirebaseMessaging.getInstance()

        messagerc_view = findViewById(R.id.chatRecyclerview)
        messageText = findViewById(R.id.messagebox)
        send_message = findViewById(R.id.btnsend)

        senderMessagelist = ArrayList()
        receiverMessagelist = ArrayList()
        messageAdapter = MessageAdapter(this, senderMessagelist)

        supportActionBar?.title = name

        databaseref = FirebaseDatabase.getInstance().getReference()

        messagerc_view.layoutManager = LinearLayoutManager(this)
        messagerc_view.adapter = messageAdapter

        firebaseMessaging.subscribeToTopic(receiveruid)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            // Save the token or send it to your server
        }



        databaseref.child("chats").child(sender_room!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    senderMessagelist.clear()
                    for (postSnapshot in snapshot.children) {
                        val messaget = postSnapshot.getValue(Message::class.java)
                        senderMessagelist.add(messaget!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                    val latestMessage = senderMessagelist.lastOrNull()
                    // For sender messages
                    if ((latestMessage != null) && (latestMessage.senderId != senderuid)) {
                        // Display notification for the latest message
                        showNotification(name, latestMessage.message)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                }
            })


        send_message.setOnClickListener {
            val message = messageText.text.toString()
            val messageobject = Message(message, senderuid)

            databaseref.child("chats").child(sender_room!!).child("messages").push()
                .setValue(messageobject).addOnSuccessListener {
                    databaseref.child("chats").child(receiver_room!!).child("messages").push()
                        .setValue(messageobject)
                }

            messageText.setText("")
        }

    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "YourChannelId"
        val channelName = "YourChannelName"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.chat)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if running on Android Oreo or higher to create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    override fun onDestroy() {
        super.onDestroy()
        firebaseMessaging.unsubscribeFromTopic(receiveruid)
    }

  inner class MyFirebaseMessagingService : FirebaseMessagingService() {
        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            // Handle FCM message received
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body

            // Display the notification
            showNotification(title, body)
        }

        override fun onNewToken(token: String) {
            // Handle updated FCM token
        }
    }

}