package mrigapps.android.test_project.Inteface.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.User


class UserAdapter(val context: Context, val userlist: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.userViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return userViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val currentuser = userlist[position]
        holder.textName.text = currentuser.uemail
    }

    class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_user)
    }
}