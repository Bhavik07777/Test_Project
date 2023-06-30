package mrigapps.android.test_project.Inteface

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import mrigapps.android.test_project.Adapter.UserAdapter
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var txtlogout: TextView
    private lateinit var dbfire: DatabaseReference
    private lateinit var progressLoader: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        mAuth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.rc_view)
        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        txtlogout = findViewById(R.id.txtlogout)
        progressLoader = findViewById(R.id.progress_loader)

        dbfire = FirebaseDatabase.getInstance().getReference()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        dbfire.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout_btn -> {
                mAuth.signOut()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                finish()
                startActivity(intent)
                true
            }
            R.id.action_profile_change -> {
                val intent = Intent(this@MainActivity, Edit_profile::class.java)
                finish()
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                mAuth.signOut()
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}