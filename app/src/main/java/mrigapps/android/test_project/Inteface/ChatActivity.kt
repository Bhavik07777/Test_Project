package mrigapps.android.test_project.Inteface

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mrigapps.android.test_project.Adapter.MessageAdapter
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var messagerc_view: RecyclerView
    private lateinit var messageText: EditText
    private lateinit var send_message: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagelist: ArrayList<Message>
    private lateinit var databaseref: DatabaseReference
    var receiver_room: String? = null
    var sender_room: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val senderuid = FirebaseAuth.getInstance().currentUser?.uid
        val name = intent.getStringExtra("name")
        val receiveruid = intent.getStringExtra("uid")

        sender_room = receiveruid + senderuid
        receiver_room = senderuid + receiveruid

        messagerc_view = findViewById(R.id.chatRecyclerview)
        messageText = findViewById(R.id.messagebox)
        send_message = findViewById(R.id.btnsend)

        messagelist = ArrayList()
        messageAdapter = MessageAdapter(this, messagelist)

        supportActionBar?.title = name

        databaseref = FirebaseDatabase.getInstance().getReference()

        messagerc_view.layoutManager = LinearLayoutManager(this)
        messagerc_view.adapter = messageAdapter


        databaseref.child("chats").child(sender_room!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messagelist.clear()
                    for (postSnapshot in snapshot.children) {
                        val messaget = postSnapshot.getValue(Message::class.java)
                        messagelist.add(messaget!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        send_message.setOnClickListener {

            val message = messageText.text.toString()
            val messageobject = Message(message,senderuid)

            databaseref.child("chats").child(sender_room!!).child("messages").push().setValue(messageobject).addOnSuccessListener {
                databaseref.child("chats").child(receiver_room!!).child("messages").push().setValue(messageobject)
            }

            messageText.setText("")
        }

    }
}