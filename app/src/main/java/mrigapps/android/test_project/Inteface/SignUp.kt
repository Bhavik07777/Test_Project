package mrigapps.android.test_project.Inteface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import mrigapps.android.test_project.R
import mrigapps.android.test_project.model.User

class SignUp : AppCompatActivity() {

    private lateinit var edname:EditText
    private lateinit var edemail:EditText
    private lateinit var edPassword:EditText
    private lateinit var btnsignup:Button
    private lateinit var btnlogin:Button


    private lateinit var mAuth:FirebaseAuth
    private lateinit var dabaseobj:DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        edemail=findViewById(R.id.edemail)
        edPassword=findViewById(R.id.edpassword)
        edname=findViewById(R.id.edName)
        btnlogin=findViewById(R.id.btnlogin)
        btnsignup=findViewById(R.id.btnSignUp)
        mAuth= FirebaseAuth.getInstance()
        btnsignup.setOnClickListener {

            val name=edname.text.toString()
            val email=edemail.text.toString()
            val password=edPassword.text.toString()

            signup(name,email,password)

        }

    }

    private fun signup(name: String, email:String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    adduserDatabase(name,email, mAuth.currentUser?.uid!!)
                    val intent=Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                    Toast.makeText(this,"Sign Up Succesfully ",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext,"Please first enter details ",Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun adduserDatabase(name: String, email: String, uid: String) {
            dabaseobj=FirebaseDatabase.getInstance().getReference()

        dabaseobj.child("users").child(uid).setValue(User(uid,email,name))
    }



}