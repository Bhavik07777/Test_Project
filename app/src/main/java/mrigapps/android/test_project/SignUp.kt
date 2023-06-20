package mrigapps.android.test_project

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUp : AppCompatActivity() {

    private lateinit var edname:EditText
    private lateinit var edemail:EditText
    private lateinit var edPassword:EditText
    private lateinit var btnsignup:Button


    private lateinit var mAuth:FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)





        edname=findViewById(R.id.edemail)
        edemail=findViewById(R.id.edpassword)
        edname=findViewById(R.id.edName)
        edPassword=findViewById(R.id.btnlogin)
        btnsignup=findViewById(R.id.btnSignUp)

        mAuth= FirebaseAuth.getInstance()



        btnsignup.setOnClickListener {

            val email=edemail.text.toString()
            val password=edPassword.text.toString()


            signup(email,password)

        }

    }

    private fun signup(email:String,password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent=Intent(this@SignUp,MainActivity::class.java)
                    startActivity(intent)
                } else {

                    Toast.makeText(applicationContext,"Please first enter details ",Toast.LENGTH_LONG).show()
                }
            }
    }


}