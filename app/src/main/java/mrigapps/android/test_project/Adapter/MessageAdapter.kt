package mrigapps.android.test_project.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.Message

class MessageAdapter(val context: Context, val messagelist: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val item_receive = 1;
    val item_sent = 0;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return sentViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messagelist[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId) ) {
            return item_sent
        } else {
            return item_receive
        }
    }
    override fun getItemCount(): Int {

        return messagelist.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messagelist[position]
        if (holder.javaClass == sentViewHolder::class.java) {

            val viewHolder = holder as sentViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveViewHolder
            holder.recevieMessage.text = currentMessage.message
        }

    }




    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.text_send_message)

    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recevieMessage = itemView.findViewById<TextView>(R.id.text_receive_message)
    }
}