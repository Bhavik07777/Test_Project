package mrigapps.android.test_project.Inteface

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
    private var receiver_room: String? = null
    private var sender_room: String? = null
    private lateinit var firebaseMessaging: FirebaseMessaging
    private lateinit var receiveruid: String
    private var senderuid: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationManager: NotificationManager
    private var notificationId: Int = 0
    private lateinit var myFirebaseMessagingService: MyFirebaseMessagingService
    private val channelId = "messageChannel"
    private val channelName = "messageChannelN"
    private lateinit var typingIndicatorRef: DatabaseReference
    private var isTyping: Boolean = false
    private lateinit var typingIndicatorTimer: CountDownTimer
    private lateinit var typingIndicatorText: ImageView

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

        typingIndicatorText = findViewById(R.id.typingIndicator)
        senderMessagelist = ArrayList()
        receiverMessagelist = ArrayList()
        messageAdapter = MessageAdapter(this, senderMessagelist)

        supportActionBar?.title = name
        myFirebaseMessagingService = MyFirebaseMessagingService()
        databaseref = FirebaseDatabase.getInstance().getReference()

        messagerc_view.layoutManager = LinearLayoutManager(this)
        messagerc_view.adapter = messageAdapter

        firebaseMessaging.subscribeToTopic(receiveruid)

        sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Retrieve the last notification ID from shared preferences
        notificationId = sharedPreferences.getInt("lastNotificationId", 0)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }




        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            myFirebaseMessagingService.onNewToken(token)
        }

        typingIndicatorRef = databaseref.child("chats").child(sender_room!!).child("typing")
        typingIndicatorRef.setValue(false) // Initialize typing indicator to false

        typingIndicatorTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                isTyping = false
                typingIndicatorRef.setValue(isTyping)
            }
        }

        messageText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val typingText = s.toString().trim()

                if (typingText.isNotEmpty()) {
                    if (!isTyping) {
                        isTyping = true
                        typingIndicatorRef.setValue(isTyping)
                    }

                    typingIndicatorTimer.cancel()
                    typingIndicatorTimer.start()
                } else {
                    typingIndicatorTimer.cancel()
                    isTyping = false
                    typingIndicatorRef.setValue(isTyping)
                }

                // Show or hide the typing indicator
                typingIndicatorText.visibility = if (isTyping) View.VISIBLE else View.GONE
            }
        })

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

        typingIndicatorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val typing = snapshot.getValue(Boolean::class.java)
                isTyping = typing ?: false
                typingIndicatorText.visibility = if (isTyping) View.VISIBLE else View.GONE
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



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed() // Call onBackPressed() to go back to the previous activity
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        finish()
    }



    private fun showNotification(title: String?, body: String?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val existingNotification = getExistingNotification(notificationId, channelId)

        val notificationBuilder = if (existingNotification != null) {
            // Update the existing notification with new content
            existingNotification.setContentTitle(title)
            existingNotification.setContentText(body)
            existingNotification.setSmallIcon(R.drawable.chat)
            existingNotification.setAutoCancel(true)
            existingNotification
        } else {
            // Create a new notification
            val notificationIntent = Intent(this, ChatActivity::class.java)
            notificationIntent.putExtra("name", title)
            notificationIntent.putExtra("uid", receiveruid)

            val pendingIntent = PendingIntent.getActivity(
                this,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.chat)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun getExistingNotification(
        notificationId: Int,
        channelId: String
    ): NotificationCompat.Builder? {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = notificationManager.activeNotifications

        for (notification in activeNotifications) {
            if (notification.id == notificationId) {
                val contentView = notification.notification.contentView
                return NotificationCompat.Builder(this, channelId)
                    .setContent(contentView)
                //.setSmallIcon(notification.notification.smallIcon)
            }
        }

        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        // sharedPreferences.edit().remove("lastNotificationId").apply()
        notificationManager.cancel(notificationId)
        firebaseMessaging.unsubscribeFromTopic(receiveruid)
        val typingIndicatorRef = databaseref.child("chats").child(sender_room!!).child("typing")
        typingIndicatorRef.setValue(false)
    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val receiveruid = remoteMessage.data["uid"]

        val notificationIntent = Intent(this, ChatActivity::class.java)
        notificationIntent.putExtra("name", title) // Pass the name of the chat as an extra
        notificationIntent.putExtra("uid", receiveruid) // Pass the receiver's UID as an extra

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, "messageChannel")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.chat)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
    }

}