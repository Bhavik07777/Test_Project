package mrigapps.android.test_project.Inteface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mrigapps.android.test_project.Inteface.Adapter.UserAdapter
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private  lateinit var userList: ArrayList<User>
    private lateinit var mAuth:FirebaseAuth
    private lateinit var txtlogout:TextView
    private lateinit var dbfire:DatabaseReference
    private lateinit var progressLoader: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        mAuth=FirebaseAuth.getInstance()
        recyclerView=findViewById(R.id.rc_view)
        userList= ArrayList()
        adapter=UserAdapter(this,userList)
        txtlogout=findViewById(R.id.txtlogout)
        progressLoader = findViewById(R.id.progress_loader)


        txtlogout.setOnClickListener {
            mAuth.signOut()
            val intent=Intent(this@MainActivity,LoginActivity::class.java)
            finish()
            startActivity(intent)

        }
        dbfire=FirebaseDatabase.getInstance().getReference()
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=adapter

        dbfire.child("users").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser=postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid!=currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
                progressLoader.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }




}