package mrigapps.android.test_project.Inteface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.User

class MainActivity : AppCompatActivity() {



//    private lateinit var adapter: Adapter
    private lateinit var recyclerView: RecyclerView
//    private  lateinit var userList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView=findViewById(R.id.rc_view)


    }
}